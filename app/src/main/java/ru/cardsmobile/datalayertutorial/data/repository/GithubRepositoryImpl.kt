package ru.cardsmobile.datalayertutorial.data.repository

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.dto.UpdateResult
import ru.cardsmobile.datalayertutorial.data.mapper.RepositoryDatabaseEntityMapper
import ru.cardsmobile.datalayertutorial.data.mapper.RepositoryMapper
import ru.cardsmobile.datalayertutorial.data.mapper.UserNameDatabaseEntityMapper
import ru.cardsmobile.datalayertutorial.data.provider.ConnectionInfoProvider
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val databaseSource: GithubDatabaseSource,
    private val updateController: GithubUpdateController,
    private val repositoryDatabaseEntityMapper: RepositoryDatabaseEntityMapper,
    private val userNameDatabaseEntityMapper: UserNameDatabaseEntityMapper,
    private val repositoryMapper: RepositoryMapper,
    private val connectionInfoProvider: ConnectionInfoProvider
) : GithubRepository {

    private val compositeDisposable = CompositeDisposable()

    override fun observeRepositories(userName: String): Observable<GithubResult> = databaseSource
        .getLatestUserName()
        .map { it.userName }
        .switchIfEmpty(Maybe.just(userName))
        .flatMapObservable { result -> observeRepositoriesInternal(result) }
        .subscribeOn(Schedulers.io())

    private fun observeRepositoriesInternal(
        userName: String
    ): Observable<GithubResult> = databaseSource
        .observeRepositoriesByUserName(userName)
        .doOnSubscribe {
            compositeDisposable += refreshRepositories(userName).subscribe()
        }
        .map { result ->
            if (result.isEmpty()) {
                GithubResult.Empty
            } else {
                GithubResult.Success(userName, result.map { repositoryMapper.map(it) })
            }
        }

    override fun refreshRepositories(userName: String): Observable<GithubResult> = updateController
        .performUpdate(userName)
        .flatMap { updateResult ->
            when (updateResult) {
                is UpdateResult.Success -> {
                    val entities = updateResult.repositoriesDto.map { repositoryMapper.map(it) }
                    saveRepositories(userName, updateResult.repositoriesDto)
                        .andThen(
                            Observable.just(
                                GithubResult.Success(
                                    userName,
                                    entities
                                )
                            )
                        )
                }
                UpdateResult.RepeatedRequest -> {
                    Observable.just(GithubResult.Error.RepeatedRequest)
                }
            }
        }
        .onErrorResumeNext { error: Throwable ->
            if (!connectionInfoProvider.isConnectedToNetwork()) {
                Observable.just(GithubResult.Error.NoInternet)
            } else {
                Observable.error(error)
            }
        }
        .subscribeOn(Schedulers.io())

    private fun saveRepositories(
        userName: String,
        repositoriesDto: List<RepositoryDto>
    ): Completable {
        val databaseEntities =
            repositoriesDto.map { repositoryDatabaseEntityMapper.map(it, userName) }
        val userNameDb = userNameDatabaseEntityMapper.map(userName, System.currentTimeMillis())
        return databaseSource
            .saveUserName(userNameDb)
            .andThen(databaseSource.saveRepositories(databaseEntities))
    }

    override fun clearSubscriptions() {
        updateController.clearSubscriptions()
        compositeDisposable.clear()
    }
}
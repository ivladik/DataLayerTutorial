package ru.cardsmobile.datalayertutorial.data.repository

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.repository.exception.DtoMappingException
import ru.cardsmobile.datalayertutorial.data.repository.mapper.RepositoryDbMapper
import ru.cardsmobile.datalayertutorial.data.repository.model.UpdateResult
import ru.cardsmobile.datalayertutorial.data.repository.mapper.RepositoryMapper
import ru.cardsmobile.datalayertutorial.data.repository.mapper.UserNameDbMapper
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.EmptyDataException
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.InvalidDataException
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.NoInternetException
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.RepeatedRequestException
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import java.net.UnknownHostException
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val databaseSource: GithubDatabaseSource,
    private val networkUpdateController: GithubNetworkUpdateController,
    private val repositoryDbMapper: RepositoryDbMapper,
    private val userNameDbMapper: UserNameDbMapper,
    private val repositoryMapper: RepositoryMapper
) : GithubRepository, GithubNetworkUpdateController.OnRepositoriesLoaded {

    private val compositeDisposable = CompositeDisposable()

    override fun getLatestUserName(): Maybe<String> = databaseSource
        .getLatestUserName()
        .map { it.userName }
        .subscribeOn(Schedulers.io())

    override fun observeGithubResult(userName: String): Observable<GithubResult> = databaseSource
        .observeRepositoriesByUserName(userName)
        .doOnSubscribe {
            compositeDisposable += refreshGithubResult(userName).subscribeBy(
                onError = {
                    Log.d(LOG_TAG, "Error while refreshing repositories: $it")
                }
            )
        }
        .flatMap { result ->
            if (result.isEmpty()) {
                Observable.error(EmptyDataException())
            } else {
                Observable.just(GithubResult(userName, result.map { repositoryMapper.map(it) }))
            }
        }
        .subscribeOn(Schedulers.io())

    override fun refreshGithubResult(userName: String): Observable<GithubResult> =
        networkUpdateController
            .performUpdate(userName, this)
            .flatMap { updateResult ->
                when (updateResult) {
                    is UpdateResult.Success -> {
                        val entities = updateResult.repositoriesDto.map { repositoryMapper.map(it) }
                        Observable.just(GithubResult(userName, entities))
                    }
                    UpdateResult.AlreadyInProgress -> {
                        Observable.error(RepeatedRequestException())
                    }
                }
            }
            .onErrorResumeNext { error: Throwable ->
                when (error) {
                    is UnknownHostException -> {
                        Observable.error(NoInternetException())
                    }
                    is DtoMappingException -> {
                        Observable.error(InvalidDataException())
                    }
                    else -> {
                        Observable.error(error)
                    }
                }
            }
            .subscribeOn(Schedulers.io())

    override fun onRepositoriesLoaded(
        userName: String,
        repositoriesDto: List<RepositoryDto>
    ): Completable = Single
        .fromCallable {
            val repositoriesDb =
                repositoriesDto.map {
                    repositoryDbMapper.map(it, userName)
                }
            val userNameDb = userNameDbMapper.map(userName, System.currentTimeMillis())
            userNameDb to repositoriesDb
        }
        .flatMapCompletable { (userNameDb, repositoriesDb) ->
            databaseSource.saveRepositories(repositoriesDb, userNameDb)
        }

    private companion object {

        const val LOG_TAG = "GithubRepositoryImpl"
    }
}
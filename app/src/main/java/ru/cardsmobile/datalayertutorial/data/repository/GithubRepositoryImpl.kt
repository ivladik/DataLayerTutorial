package ru.cardsmobile.datalayertutorial.data.repository

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Singles
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.repository.exception.DtoMappingException
import ru.cardsmobile.datalayertutorial.data.repository.mapper.RepositoryDbMapper
import ru.cardsmobile.datalayertutorial.data.repository.mapper.RepositoryMapper
import ru.cardsmobile.datalayertutorial.data.repository.mapper.UserNameDbMapper
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.source.database.dto.UserNameDb
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.domain.entity.Repository
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.InvalidDataException
import ru.cardsmobile.datalayertutorial.domain.exception.GithubException.NoInternetException
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import java.net.UnknownHostException
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val databaseSource: GithubDatabaseSource,
    private val networkUpdateController: NetworkUpdateController<String, List<RepositoryDto>>,
    private val repositoryDbMapper: RepositoryDbMapper,
    private val userNameDbMapper: UserNameDbMapper,
    private val repositoryMapper: RepositoryMapper
) : GithubRepository {

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
                    Log.e(LOG_TAG, "Error while refreshing repositories: $it")
                }
            )
        }
        .map { result -> result.map { repositoryMapper.map(it) } }
        .flatMap { repositories ->
            Observable.just(GithubResult(userName, repositories))
        }
        .subscribeOn(Schedulers.io())

    override fun refreshGithubResult(
        userName: String
    ): Single<GithubResult> = networkUpdateController
        .performUpdate(userName)
        .map { result -> result.map { repositoryMapper.map(it) } }
        .flatMap { repositories ->
            saveRepositories(userName, repositories)
                .andThen(Single.just(GithubResult(userName, repositories)))
        }
        .onErrorResumeNext { error: Throwable ->
            when (error) {
                is UnknownHostException -> {
                    Single.error(NoInternetException())
                }
                is DtoMappingException -> {
                    Single.error(InvalidDataException())
                }
                else -> {
                    Single.error(error)
                }
            }
        }
        .subscribeOn(Schedulers.io())

    private fun saveRepositories(
        userName: String,
        repositories: List<Repository>
    ): Completable = Singles
        .zip(
            Single.fromCallable {
                repositories.map { repositoryDbMapper.map(it, userName) }
            },
            Single.fromCallable {
                userNameDbMapper.map(userName, System.currentTimeMillis())
            }
        ) { repositoriesDb: List<RepositoryDb>, userNameDb: UserNameDb ->
            repositoriesDb to userNameDb
        }
        .flatMapCompletable { (repositoriesDb, userNameDb) ->
            databaseSource.saveRepositories(repositoriesDb, userNameDb)
        }

    private companion object {

        const val LOG_TAG = "GithubRepositoryImpl"
    }
}
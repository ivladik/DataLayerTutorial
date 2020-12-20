package ru.cardsmobile.datalayertutorial.data.repository

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.model.UpdateResult
import ru.cardsmobile.datalayertutorial.data.mapper.RepositoryMapper
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import java.net.UnknownHostException
import javax.inject.Inject

class GithubRepositoryImpl @Inject constructor(
    private val databaseSource: GithubDatabaseSource,
    private val updateController: GithubUpdateController,
    private val repositoryMapper: RepositoryMapper
) : GithubRepository {

    private val compositeDisposable = CompositeDisposable()

    override fun getLatestUserName(): Maybe<String> = databaseSource
        .getLatestUserName()
        .map { it.userName }
        .subscribeOn(Schedulers.io())

    override fun observeRepositories(userName: String): Observable<GithubResult> = databaseSource
        .observeRepositoriesByUserName(userName)
        .doOnSubscribe {
            compositeDisposable += refreshRepositories(userName).subscribeBy(
                onError = {
                    Log.d(LOG_TAG, "Error while refreshing repositories: $it")
                }
            )
        }
        .map { result ->
            if (result.isEmpty()) {
                GithubResult.Empty
            } else {
                GithubResult.Success(userName, result.map { repositoryMapper.map(it) })
            }
        }
        .subscribeOn(Schedulers.io())

    override fun refreshRepositories(userName: String): Observable<GithubResult> = updateController
        .performUpdate(userName)
        .flatMap { updateResult ->
            when (updateResult) {
                is UpdateResult.Success -> {
                    val entities = updateResult.repositoriesDto.map { repositoryMapper.map(it) }
                    Observable.just(
                        GithubResult.Success(
                            userName,
                            entities
                        )
                    )
                }
                UpdateResult.AlreadyInProgress -> {
                    Observable.just(GithubResult.Error.RepeatedRequest)
                }
            }
        }
        .onErrorResumeNext { error: Throwable ->
            if (error is UnknownHostException) {
                Observable.just(GithubResult.Error.NoInternet)
            } else {
                Observable.error(error)
            }
        }
        .subscribeOn(Schedulers.io())

    private companion object {

        const val LOG_TAG = "GithubRepositoryImpl"
    }
}
package ru.cardsmobile.datalayertutorial.data.repository

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.model.PutResult
import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.model.UpdateResult
import ru.cardsmobile.datalayertutorial.data.mapper.RepositoryDatabaseEntityMapper
import ru.cardsmobile.datalayertutorial.data.mapper.UserNameDatabaseEntityMapper
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import ru.cardsmobile.datalayertutorial.data.source.GithubNetworkSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GithubUpdateController @Inject constructor(
    private val updateRegistry: GithubUpdateRegistry,
    private val networkSource: GithubNetworkSource,
    private val databaseSource: GithubDatabaseSource,
    private val repositoryDatabaseEntityMapper: RepositoryDatabaseEntityMapper,
    private val userNameDatabaseEntityMapper: UserNameDatabaseEntityMapper
) {

    private val compositeDisposable = CompositeDisposable()

    fun performUpdate(userName: String): Observable<UpdateResult> = Observable
        .create { emitter ->
            if (updateRegistry.putToQueue(userName, emitter) == PutResult.AlreadyInQueue) {
                emitter.onNext(UpdateResult.AlreadyInProgress)
            } else {
                compositeDisposable += getRepositories(userName)
                    .flatMapCompletable {
                        saveRepositories(userName, it)
                            .andThen(notifyEmitters(userName, it))
                    }
                    .onErrorResumeNext { throwable ->
                        notifyEmitters(userName, throwable)
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                        onError = {
                            Log.d(LOG_TAG, "Error while getting repositories: $it")
                        }
                    )
            }
        }

    private fun getRepositories(userName: String): Single<List<RepositoryDto>> = Single
        .timer(1, TimeUnit.SECONDS) // искуственная задержка
        .flatMap { networkSource.getRepositories(userName) }

    private fun saveRepositories(
        userName: String,
        repositoriesDto: List<RepositoryDto>
    ): Completable {
        val repositoriesDb =
            repositoriesDto.map {
                repositoryDatabaseEntityMapper.map(it, userName)
            }
        val userNameDb = userNameDatabaseEntityMapper.map(userName, System.currentTimeMillis())
        return databaseSource.saveRepositories(repositoriesDb, userNameDb)
    }

    private fun notifyEmitters(
        userName: String,
        repositoriesDto: List<RepositoryDto>
    ): Completable {
        val emitters = updateRegistry.pollEmitters(userName)
        return Completable.fromAction {
            emitters.forEach {
                it.onNext(UpdateResult.Success(repositoriesDto))
                it.onComplete()
            }
        }
    }

    private fun notifyEmitters(userName: String, throwable: Throwable): Completable {
        val emitters = updateRegistry.pollEmitters(userName)
        return Completable.fromAction {
            emitters.forEach {
                it.tryOnError(throwable)
            }
        }
    }

    private companion object {

        const val LOG_TAG = "GithubUpdateController"
    }
}
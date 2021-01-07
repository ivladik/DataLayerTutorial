package ru.cardsmobile.datalayertutorial.data.repository

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.repository.model.PutResult
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.repository.model.UpdateResult
import ru.cardsmobile.datalayertutorial.data.source.GithubNetworkSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GithubNetworkUpdateController @Inject constructor(
    private val updateRegistry: GithubUpdateRegistry,
    private val networkSource: GithubNetworkSource
) {

    private val compositeDisposable = CompositeDisposable()

    fun performUpdate(
        userName: String,
        callback: OnRepositoriesLoaded? = null
    ): Observable<UpdateResult> = Observable
        .create { emitter ->
            if (updateRegistry.putToQueue(userName, emitter) == PutResult.AlreadyInQueue) {
                emitter.onNext(UpdateResult.AlreadyInProgress)
            } else {
                compositeDisposable += getRepositories(userName)
                    .flatMapCompletable {
                        (callback
                            ?.onRepositoriesLoaded(userName, it)
                            ?.onErrorComplete()
                            ?: Completable.complete())
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

    private fun notifyEmitters(
        userName: String,
        repositoriesDto: List<RepositoryDto>
    ): Completable =
        Completable.fromAction {
            updateRegistry.pollEmitters(userName).forEach {
                it.onNext(UpdateResult.Success(repositoriesDto))
                it.onComplete()
            }
        }

    private fun notifyEmitters(userName: String, throwable: Throwable): Completable =
        Completable.fromAction {
            updateRegistry.pollEmitters(userName).forEach {
                it.tryOnError(throwable)
            }
        }

    interface OnRepositoriesLoaded {

        fun onRepositoriesLoaded(
            userName: String,
            repositoriesDto: List<RepositoryDto>
        ): Completable
    }

    private companion object {

        const val LOG_TAG = "GithubUpdateController"
    }
}
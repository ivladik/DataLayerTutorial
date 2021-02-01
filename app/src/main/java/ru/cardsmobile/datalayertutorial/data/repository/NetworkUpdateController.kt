package ru.cardsmobile.datalayertutorial.data.repository

import android.util.Log
import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.repository.UpdateRegistry.*
import ru.cardsmobile.datalayertutorial.data.source.NetworkSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class NetworkUpdateController<P, R> @Inject constructor(
    private val updateRegistry: UpdateRegistry<P, R>,
    private val networkSource: NetworkSource<P, R>
) {

    private val compositeDisposable = CompositeDisposable()

    fun performUpdate(parameter: P): Single<R> = Single
        .create { emitter ->
            if (updateRegistry.putToQueue(parameter, emitter) == PutResult.DownloadNeeded) {
                compositeDisposable += Single
                    .timer(1, TimeUnit.SECONDS, Schedulers.io()) // искуственная задержка
                    .flatMap { networkSource.performRequest(parameter) }
                    .flatMapCompletable {
                        notifyEmitters(parameter, it)
                    }
                    .onErrorResumeNext { throwable ->
                        notifyEmitters(parameter, throwable)
                    }
//                    .subscribeOn(Schedulers.io())
                    .subscribeBy(
                        onError = {
                            Log.d(LOG_TAG, "Error while getting repositories: $it")
                        }
                    )
            }
        }

    private fun notifyEmitters(
        parameter: P,
        result: R
    ): Completable =
        Completable.fromAction {
            updateRegistry.pollEmitters(parameter).forEach {
                it.onSuccess(result)
            }
        }

    private fun notifyEmitters(parameter: P, throwable: Throwable): Completable =
        Completable.fromAction {
            updateRegistry.pollEmitters(parameter).forEach {
                it.tryOnError(throwable)
            }
        }

    private companion object {

        const val LOG_TAG = "GithubUpdateController"
    }
}
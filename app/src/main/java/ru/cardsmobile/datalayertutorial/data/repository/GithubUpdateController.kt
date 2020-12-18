package ru.cardsmobile.datalayertutorial.data.repository

import io.reactivex.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import ru.cardsmobile.datalayertutorial.data.dto.PutResult
import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.dto.UpdateResult
import ru.cardsmobile.datalayertutorial.data.source.GithubNetworkSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GithubUpdateController @Inject constructor(
    private val updateRegistry: GithubUpdateRegistry,
    private val networkSource: GithubNetworkSource
) {

    private val compositeDisposable = CompositeDisposable()

    fun performUpdate(userName: String): Observable<UpdateResult> = Observable
        .create { emitter ->
            if (updateRegistry.putToQueue(userName, emitter) == PutResult.AlreadyInQueue) {
                emitter.onNext(UpdateResult.RepeatedRequest)
            } else {
                compositeDisposable += getRepositories(userName)
                    .flatMapCompletable {
                        notifyEmitters(userName, it)
                    }
                    .onErrorResumeNext { throwable ->
                        notifyEmitters(userName, throwable)
                    }
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            }
        }

    fun clearSubscriptions() =
        compositeDisposable.clear()

    private fun getRepositories(userName: String): Single<List<RepositoryDto>> = Single
        .timer(1, TimeUnit.SECONDS) // искуственная задержка
        .flatMap { networkSource.getRepositories(userName) }

    private fun notifyEmitters(userName: String, repositoriesDto: List<RepositoryDto>): Completable {
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
}
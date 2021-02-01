package ru.cardsmobile.datalayertutorial.data.repository

import io.reactivex.SingleEmitter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateRegistry<P, R> @Inject constructor() {

    private val requestsInProgress = mutableListOf<RegistryItem<P, R>>()

    @Synchronized
    fun putToQueue(parameter: P, emitter: SingleEmitter<R>): PutResult {
        val isOtherRequestsInProgress = requestsInProgress.any { parameter == it.parameter }
        requestsInProgress.add(RegistryItem(parameter, emitter))
        return if (isOtherRequestsInProgress) {
            PutResult.AlreadyInQueue
        } else {
            PutResult.DownloadNeeded
        }
    }

    @Synchronized
    fun pollEmitters(parameter: P): List<SingleEmitter<R>> {
        val emitters = mutableListOf<SingleEmitter<R>>()
        val mutableIterator = requestsInProgress.iterator()
        for (registryItem in mutableIterator) {
            if (registryItem.parameter == parameter) {
                emitters.add(registryItem.emitter)
                mutableIterator.remove()
            }
        }
        return emitters
    }

    sealed class PutResult {

        object AlreadyInQueue : PutResult()

        object DownloadNeeded : PutResult()
    }

    data class RegistryItem<P, R>(
        val parameter: P,
        val emitter: SingleEmitter<R>
    )
}
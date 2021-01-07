package ru.cardsmobile.datalayertutorial.data.repository

import io.reactivex.ObservableEmitter
import ru.cardsmobile.datalayertutorial.data.repository.model.PutResult
import ru.cardsmobile.datalayertutorial.data.repository.model.RegistryItem
import ru.cardsmobile.datalayertutorial.data.repository.model.UpdateResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GithubUpdateRegistry @Inject constructor() {

    private val requestsInProgress = mutableListOf<RegistryItem>()

    @Synchronized
    fun putToQueue(userName: String, emitter: ObservableEmitter<UpdateResult>): PutResult {
        val isOtherRequestsInProgress = requestsInProgress.any { userName == it.userName }
        requestsInProgress.add(RegistryItem(userName, emitter))
        return if (isOtherRequestsInProgress) {
            PutResult.AlreadyInQueue
        } else {
            PutResult.DownloadNeeded
        }
    }

    @Synchronized
    fun pollEmitters(userName: String): List<ObservableEmitter<UpdateResult>> {
        val emitters = mutableListOf<ObservableEmitter<UpdateResult>>()
        val mutableIterator = requestsInProgress.iterator()
        for (registryItem in mutableIterator) {
            if (registryItem.userName == userName) {
                emitters.add(registryItem.emitter)
                mutableIterator.remove()
            }
        }
        return emitters
    }
}
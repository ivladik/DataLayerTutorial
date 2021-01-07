package ru.cardsmobile.datalayertutorial.data.repository.model

import io.reactivex.ObservableEmitter
import ru.cardsmobile.datalayertutorial.data.repository.model.UpdateResult

data class RegistryItem(
    val userName: String,
    val emitter: ObservableEmitter<UpdateResult>
)
package ru.cardsmobile.datalayertutorial.data.model

import io.reactivex.ObservableEmitter

data class RegistryItem(
    val userName: String,
    val emitter: ObservableEmitter<UpdateResult>
)
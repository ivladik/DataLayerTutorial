package ru.cardsmobile.datalayertutorial.data.dto

import io.reactivex.ObservableEmitter

data class RegistryItem(
    val userName: String,
    val emitter: ObservableEmitter<UpdateResult>
)
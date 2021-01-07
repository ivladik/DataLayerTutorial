package ru.cardsmobile.datalayertutorial.data.repository.model

sealed class PutResult {

    object AlreadyInQueue : PutResult()

    object DownloadNeeded : PutResult()
}
package ru.cardsmobile.datalayertutorial.data.model

sealed class PutResult {

    object AlreadyInQueue : PutResult()

    object DownloadNeeded : PutResult()
}
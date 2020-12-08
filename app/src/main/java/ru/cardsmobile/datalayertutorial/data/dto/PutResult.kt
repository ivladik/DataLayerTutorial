package ru.cardsmobile.datalayertutorial.data.dto

sealed class PutResult {

    object AlreadyInQueue : PutResult()

    object DownloadNeeded : PutResult()
}
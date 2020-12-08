package ru.cardsmobile.datalayertutorial.data.dto

sealed class UpdateResult {

    data class Success(val repositoriesDto: List<RepositoryDto>) : UpdateResult()

    object RepeatedRequest : UpdateResult()
}
package ru.cardsmobile.datalayertutorial.data.model

import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto

sealed class UpdateResult {

    data class Success(val repositoriesDto: List<RepositoryDto>) : UpdateResult()

    object AlreadyInProgress : UpdateResult()
}
package ru.cardsmobile.datalayertutorial.data.repository.model

import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto

sealed class UpdateResult {

    data class Success(val repositoriesDto: List<RepositoryDto>) : UpdateResult()

    object AlreadyInProgress : UpdateResult()
}
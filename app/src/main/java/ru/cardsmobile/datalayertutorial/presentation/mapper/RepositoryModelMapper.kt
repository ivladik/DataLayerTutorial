package ru.cardsmobile.datalayertutorial.presentation.mapper

import ru.cardsmobile.datalayertutorial.domain.entity.Repository
import ru.cardsmobile.datalayertutorial.presentation.model.RepositoryModel
import javax.inject.Inject

class RepositoryModelMapper @Inject constructor() {

    fun map(repository: Repository): RepositoryModel =
        RepositoryModel(repository.name, repository.starsCount, repository.forksCount)
}
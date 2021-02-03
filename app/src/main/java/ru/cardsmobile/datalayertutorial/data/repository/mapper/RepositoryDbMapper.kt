package ru.cardsmobile.datalayertutorial.data.repository.mapper

import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb
import ru.cardsmobile.datalayertutorial.domain.entity.Repository
import javax.inject.Inject

class RepositoryDbMapper @Inject constructor() {

    fun map(
        repository: Repository,
        userName: String
    ): RepositoryDb =
        RepositoryDb(
            id = repository.id,
            userName = userName,
            starsCount = repository.starsCount,
            forksCount = repository.forksCount,
            name = repository.name
        )
}
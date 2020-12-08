package ru.cardsmobile.datalayertutorial.data.mapper

import ru.cardsmobile.datalayertutorial.data.database.entity.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.exception.DtoMappingException
import ru.cardsmobile.datalayertutorial.domain.entity.Repository
import javax.inject.Inject

class RepositoryMapper @Inject constructor() {

    fun map(repositoryDb: RepositoryDb): Repository =
        Repository(
            repositoryDb.name,
            repositoryDb.starsCount,
            repositoryDb.forksCount
        )

    fun map(repositoryDto: RepositoryDto): Repository =
        Repository(
            repositoryDto.name
                ?: throw DtoMappingException("Error mapping repositoryDto.name field"),
            repositoryDto.starsCount
                ?: throw DtoMappingException("Error mapping repositoryDto.starsCount field"),
            repositoryDto.forksCount
                ?: throw DtoMappingException("Error mapping repositoryDto.forksCount field")
        )
}
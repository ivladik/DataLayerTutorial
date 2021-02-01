package ru.cardsmobile.datalayertutorial.data.repository.mapper

import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.repository.exception.DtoMappingException
import ru.cardsmobile.datalayertutorial.domain.entity.Repository
import javax.inject.Inject

class RepositoryMapper @Inject constructor() {

    fun map(repositoryDb: RepositoryDb): Repository =
        Repository(
            repositoryDb.id,
            repositoryDb.name,
            repositoryDb.starsCount,
            repositoryDb.forksCount
        )

    fun map(repositoryDto: RepositoryDto): Repository =
        Repository(
            repositoryDto.id
                ?: throw DtoMappingException("Error mapping repositoryDto.id field"),
            repositoryDto.name
                ?: throw DtoMappingException("Error mapping repositoryDto.name field"),
            repositoryDto.starsCount
                ?: throw DtoMappingException("Error mapping repositoryDto.starsCount field"),
            repositoryDto.forksCount
                ?: throw DtoMappingException("Error mapping repositoryDto.forksCount field")
        )
}
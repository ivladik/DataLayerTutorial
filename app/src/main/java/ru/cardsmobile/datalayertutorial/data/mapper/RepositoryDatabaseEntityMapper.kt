package ru.cardsmobile.datalayertutorial.data.mapper

import ru.cardsmobile.datalayertutorial.data.database.entity.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.exception.DtoMappingException
import javax.inject.Inject

class RepositoryDatabaseEntityMapper @Inject constructor() {

    fun map(
        repositoryDto: RepositoryDto,
        userName: String
    ): RepositoryDb =
        RepositoryDb(
            id = repositoryDto.id ?: throw DtoMappingException(),
            userNameId = userName ?: throw DtoMappingException(),
            starsCount = repositoryDto.starsCount ?: throw DtoMappingException(),
            forksCount = repositoryDto.forksCount ?: throw DtoMappingException(),
            name = repositoryDto.name ?: throw DtoMappingException()
        )
}
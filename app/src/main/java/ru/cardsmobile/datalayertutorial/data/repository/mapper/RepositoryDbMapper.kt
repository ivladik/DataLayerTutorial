package ru.cardsmobile.datalayertutorial.data.repository.mapper

import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.repository.exception.DtoMappingException
import javax.inject.Inject

class RepositoryDbMapper @Inject constructor() {

    fun map(
        repositoryDto: RepositoryDto,
        userName: String
    ): RepositoryDb =
        RepositoryDb(
            id = repositoryDto.id ?: throw DtoMappingException(),
            userNameId = userName,
            starsCount = repositoryDto.starsCount ?: throw DtoMappingException(),
            forksCount = repositoryDto.forksCount ?: throw DtoMappingException(),
            name = repositoryDto.name ?: throw DtoMappingException()
        )
}
package ru.cardsmobile.datalayertutorial.data.source

import io.reactivex.Single
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto

interface GithubNetworkSource {

    fun getRepositories(userName: String): Single<List<RepositoryDto>>
}
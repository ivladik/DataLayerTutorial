package ru.cardsmobile.datalayertutorial.data.source.network

import io.reactivex.Single
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.source.GithubNetworkSource
import javax.inject.Inject

class GithubNetworkSourceImpl @Inject constructor(
    private val githubApi: GithubApi
) : GithubNetworkSource {

    override fun getRepositories(userName: String): Single<List<RepositoryDto>> =
        githubApi.getRepositories(userName)
}
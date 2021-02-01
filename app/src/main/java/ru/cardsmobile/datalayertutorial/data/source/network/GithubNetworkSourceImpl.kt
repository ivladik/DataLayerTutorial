package ru.cardsmobile.datalayertutorial.data.source.network

import io.reactivex.Single
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.data.source.NetworkSource
import javax.inject.Inject

class GithubNetworkSourceImpl @Inject constructor(
    private val githubApi: GithubApi
) : NetworkSource<String, List<@JvmSuppressWildcards RepositoryDto>> {

    override fun performRequest(parameter: String): Single<List<RepositoryDto>> = githubApi.getRepositories(parameter)
}
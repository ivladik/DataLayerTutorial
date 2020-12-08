package ru.cardsmobile.datalayertutorial.data.source

import io.reactivex.Single
import ru.cardsmobile.datalayertutorial.data.api.GithubApi
import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto
import javax.inject.Inject

class GithubNetworkSourceImpl @Inject constructor(
    private val githubApi: GithubApi
) : GithubNetworkSource {

    override fun getRepositories(userName: String): Single<List<RepositoryDto>> =
        githubApi.getRepositories(userName)
}
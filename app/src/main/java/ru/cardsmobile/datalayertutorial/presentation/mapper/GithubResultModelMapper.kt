package ru.cardsmobile.datalayertutorial.presentation.mapper

import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.presentation.model.GithubResultModel
import javax.inject.Inject

class GithubResultModelMapper @Inject constructor(
    private val repositoryModelMapper: RepositoryModelMapper
) {

    fun map(githubResult: GithubResult): GithubResultModel =
        when (githubResult) {
            is GithubResult.Success -> GithubResultModel.Success(
                githubResult.userName,
                githubResult.repository.map { repositoryModelMapper.map(it) }
            )
            GithubResult.Empty -> GithubResultModel.Empty
            GithubResult.Error.RepeatedRequest -> GithubResultModel.Error.RepeatedRequest
            GithubResult.Error.NoInternet -> GithubResultModel.Error.NoInternetException
        }
}
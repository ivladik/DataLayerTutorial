package ru.cardsmobile.datalayertutorial.presentation.model

sealed class GithubResultModel {

    data class Success(val userName: String, val repository: List<RepositoryModel>): GithubResultModel()

    object Empty : GithubResultModel()

    object Loading : GithubResultModel()

    sealed class Error : GithubResultModel() {

        object RepeatedRequest : Error()

        object NoInternetException : Error()

        object Unexpected : Error()
    }
}
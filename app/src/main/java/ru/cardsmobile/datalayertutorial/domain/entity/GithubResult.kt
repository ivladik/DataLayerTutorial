package ru.cardsmobile.datalayertutorial.domain.entity

sealed class GithubResult {

    data class Success(val userName: String, val repository: List<Repository>) : GithubResult()

    object Empty : GithubResult()

    sealed class Error : GithubResult() {

        object RepeatedRequest : Error()

        object NoInternet : Error()
    }
}
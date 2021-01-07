package ru.cardsmobile.datalayertutorial.presentation.model

import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult

sealed class GithubState {

    data class Success(internal val githubResult: GithubResult) : GithubState()

    object Loading : GithubState()

    data class Error(val throwable: Throwable) : GithubState()
}
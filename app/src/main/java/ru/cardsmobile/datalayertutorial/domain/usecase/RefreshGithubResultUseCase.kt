package ru.cardsmobile.datalayertutorial.domain.usecase

import io.reactivex.Single
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import javax.inject.Inject

class RefreshGithubResultUseCase @Inject constructor(
    private val repository: GithubRepository
) {

    operator fun invoke(userName: String): Single<GithubResult> =
        repository.refreshGithubResult(userName)
}
package ru.cardsmobile.datalayertutorial.domain.usecase

import io.reactivex.Observable
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import javax.inject.Inject

class GetRepositoryUseCase @Inject constructor(
    private val githubRepository: GithubRepository
) {

    operator fun invoke(): Observable<GithubResult> =
        githubRepository.observeRepositories("JakeWharton")
}
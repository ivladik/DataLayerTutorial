package ru.cardsmobile.datalayertutorial.domain.usecase

import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import javax.inject.Inject

class ClearSubscriptionsUseCase @Inject constructor(
    private val githubRepository: GithubRepository
) {

    operator fun invoke() = githubRepository.clearSubscriptions()
}
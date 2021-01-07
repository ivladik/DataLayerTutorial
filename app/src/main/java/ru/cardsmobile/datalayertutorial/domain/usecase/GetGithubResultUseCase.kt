package ru.cardsmobile.datalayertutorial.domain.usecase

import io.reactivex.Maybe
import io.reactivex.Observable
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import javax.inject.Inject

class GetGithubResultUseCase @Inject constructor(
    private val githubRepository: GithubRepository
) {

    operator fun invoke(): Observable<GithubResult> = githubRepository
        .getLatestUserName()
        .switchIfEmpty(Maybe.just("JakeWharton"))
        .flatMapObservable { userName -> githubRepository.observeGithubResult(userName) }
}
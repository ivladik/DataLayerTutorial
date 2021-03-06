package ru.cardsmobile.datalayertutorial.domain.repository

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.Single
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult


interface GithubRepository {

    fun getLatestUserName(): Maybe<String>

    fun observeGithubResult(userName: String): Observable<GithubResult>

    fun refreshGithubResult(userName: String): Single<GithubResult>
}
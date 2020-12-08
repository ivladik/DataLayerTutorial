package ru.cardsmobile.datalayertutorial.domain.repository

import io.reactivex.Observable
import ru.cardsmobile.datalayertutorial.domain.entity.GithubResult


interface GithubRepository {

    fun observeRepositories(userName: String): Observable<GithubResult>

    fun refreshRepositories(userName: String): Observable<GithubResult>

    fun clearSubscriptions()
}
package ru.cardsmobile.datalayertutorial.data.api

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import ru.cardsmobile.datalayertutorial.data.dto.RepositoryDto

interface GithubApi {

    @GET("/users/{user}/repos")
    fun getRepositories(@Path("user") user: String): Single<List<RepositoryDto>>
}
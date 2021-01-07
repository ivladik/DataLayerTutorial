package ru.cardsmobile.datalayertutorial.data.source.network

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto

interface GithubApi {

    @GET("/users/{user}/repos")
    fun getRepositories(@Path("user") user: String): Single<List<RepositoryDto>>
}
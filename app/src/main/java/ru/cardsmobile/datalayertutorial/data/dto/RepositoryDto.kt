package ru.cardsmobile.datalayertutorial.data.dto

import com.google.gson.annotations.SerializedName

data class RepositoryDto(
    @SerializedName("id")
    val id: Long?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("stargazers_count")
    val starsCount: Int?,
    @SerializedName("forks_count")
    val forksCount: Int?
)
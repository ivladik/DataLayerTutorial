package ru.cardsmobile.datalayertutorial.domain.entity

data class Repository(
    val id: Long,
    val name: String,
    val starsCount: Int,
    val forksCount: Int
)
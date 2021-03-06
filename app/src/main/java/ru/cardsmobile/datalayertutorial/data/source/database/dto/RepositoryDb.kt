package ru.cardsmobile.datalayertutorial.data.source.database.dto

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Repository",
    foreignKeys = [
        ForeignKey(
            entity = UserNameDb::class,
            parentColumns = ["userName"],
            childColumns = ["userName"]
        )
    ]
)
data class RepositoryDb(
    @PrimaryKey
    val id: Long,
    val userName: String,
    val name: String,
    val starsCount: Int,
    val forksCount: Int
)
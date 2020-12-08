package ru.cardsmobile.datalayertutorial.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "Repository",
    foreignKeys = [
        ForeignKey(
            entity = UserNameDb::class,
            parentColumns = ["userName"],
            childColumns = ["userNameId"]
        )
    ]
)
data class RepositoryDb(
    @PrimaryKey
    val id: Long,
    val userNameId: String,
    val name: String,
    val starsCount: Int,
    val forksCount: Int
)
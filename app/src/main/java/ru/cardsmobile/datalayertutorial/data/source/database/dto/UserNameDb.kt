package ru.cardsmobile.datalayertutorial.data.source.database.dto

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "UserName",
    indices = [
        Index(value = ["userName"], unique = true)
    ]
)
data class UserNameDb(
    val userName: String,
    val updatedAt: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}
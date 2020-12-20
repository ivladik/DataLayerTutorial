package ru.cardsmobile.datalayertutorial.data.database.entity

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
    val timeStamp: Long
) {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
}
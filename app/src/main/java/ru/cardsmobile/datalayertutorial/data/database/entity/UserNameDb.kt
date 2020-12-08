package ru.cardsmobile.datalayertutorial.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserName")
data class UserNameDb(
    @PrimaryKey
    val userName: String,
    val timeStamp: Long
)
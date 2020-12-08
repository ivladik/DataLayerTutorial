package ru.cardsmobile.datalayertutorial.data.mapper

import ru.cardsmobile.datalayertutorial.data.database.entity.UserNameDb
import javax.inject.Inject

class UserNameDatabaseEntityMapper @Inject constructor() {

    fun map(userName: String, timestamp: Long): UserNameDb =
        UserNameDb(userName, timestamp)
}
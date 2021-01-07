package ru.cardsmobile.datalayertutorial.data.repository.mapper

import ru.cardsmobile.datalayertutorial.data.source.database.dto.UserNameDb
import javax.inject.Inject

class UserNameDbMapper @Inject constructor() {

    fun map(userName: String, timestamp: Long): UserNameDb =
        UserNameDb(
            userName,
            timestamp
        )
}
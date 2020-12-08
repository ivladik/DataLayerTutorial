package ru.cardsmobile.datalayertutorial.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.cardsmobile.datalayertutorial.data.database.dao.RepositoryDao
import ru.cardsmobile.datalayertutorial.data.database.dao.UserNameDao
import ru.cardsmobile.datalayertutorial.data.database.entity.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.database.entity.UserNameDb

@Database(
    entities = [
        RepositoryDb::class,
        UserNameDb::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GithubDatabase : RoomDatabase() {

    abstract fun repositoryDao(): RepositoryDao

    abstract fun userNameDao(): UserNameDao

    companion object {

        fun newInstance(application: Application): GithubDatabase = Room
            .databaseBuilder(application, GithubDatabase::class.java, "TestDatabase")
            .build()
    }
}
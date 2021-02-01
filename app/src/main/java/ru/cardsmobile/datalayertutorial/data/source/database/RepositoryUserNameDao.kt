package ru.cardsmobile.datalayertutorial.data.source.database

import androidx.room.Dao
import androidx.room.Transaction
import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.source.database.dto.UserNameDb

@Dao
interface RepositoryUserNameDao {

    @Transaction
    fun saveRepositories(
        userNameDao: UserNameDao,
        repositoryDao: RepositoryDao,
        userNameDb: UserNameDb,
        repositoriesDb: List<RepositoryDb>
    ) {
        userNameDao.insertUserName(userNameDb)
        repositoryDao.deleteRepositoriesByUserName(userNameDb.userName)
        repositoryDao.insertRepositories(repositoriesDb)
    }
}
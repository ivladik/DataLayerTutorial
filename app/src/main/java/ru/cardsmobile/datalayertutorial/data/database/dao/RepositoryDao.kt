package ru.cardsmobile.datalayertutorial.data.database.dao

import androidx.room.*
import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.database.entity.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.database.entity.UserNameDb

@Dao
interface RepositoryDao {

    @Query("SELECT * FROM Repository WHERE userNameId = :userName")
    fun observeRepositoriesByUserName(userName: String): Observable<List<RepositoryDb>>

    fun observeRepositoriesByUserNameDistinct(userName: String): Observable<List<RepositoryDb>> =
        observeRepositoriesByUserName(userName).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepositories(repositoryDbs: List<RepositoryDb>)

    @Transaction
    fun insertRepositoriesTransaction(
        userNameDao: UserNameDao,
        userNameDb: UserNameDb,
        repositoriesDb: List<RepositoryDb>
    ) {
        userNameDao.insertUserName(userNameDb)
        insertRepositories(repositoriesDb)
    }
}
package ru.cardsmobile.datalayertutorial.data.database.dao

import androidx.room.*
import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.database.entity.RepositoryDb

@Dao
abstract class RepositoryDao {

    @Query("SELECT * FROM Repository WHERE userNameId = :userName")
    protected abstract fun selectRepositoriesByUserName(userName: String): Observable<List<RepositoryDb>>

    fun selectRepositoriesByUserNameDistinct(userName: String): Observable<List<RepositoryDb>> =
        selectRepositoriesByUserName(userName).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertRepositories(repositoryDbs: List<RepositoryDb>): Completable
}
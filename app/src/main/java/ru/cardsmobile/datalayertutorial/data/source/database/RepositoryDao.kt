package ru.cardsmobile.datalayertutorial.data.source.database

import androidx.room.*
import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb

@Dao
interface RepositoryDao {

    @Query("SELECT * FROM Repository WHERE userName = :userName")
    fun observeRepositoriesByUserName(userName: String): Observable<List<RepositoryDb>>

    fun observeRepositoriesByUserNameDistinct(userName: String): Observable<List<RepositoryDb>> =
        observeRepositoriesByUserName(userName).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRepositories(repositoryDbs: List<RepositoryDb>)

    @Query("DELETE FROM Repository WHERE userName = :userName")
    fun deleteRepositoriesByUserName(userName: String)
}
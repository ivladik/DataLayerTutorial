package ru.cardsmobile.datalayertutorial.data.source.database

import androidx.room.*
import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.source.database.dto.UserNameDb

@Dao
interface UserNameDao {

    @Query("SELECT * FROM UserName ORDER BY updatedAt DESC LIMIT 1")
    fun selectLatestUserName(): Maybe<UserNameDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserName(userNameDb: UserNameDb): Completable
}
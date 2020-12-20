package ru.cardsmobile.datalayertutorial.data.database.dao

import androidx.room.*
import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.database.entity.UserNameDb

@Dao
interface UserNameDao {

    @Query("SELECT * FROM UserName ORDER BY timeStamp DESC LIMIT 1")
    fun selectLatestUserName(): Maybe<UserNameDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserName(userNameDb: UserNameDb)
}
package ru.cardsmobile.datalayertutorial.data.source

import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.database.dao.RepositoryDao
import ru.cardsmobile.datalayertutorial.data.database.dao.UserNameDao
import ru.cardsmobile.datalayertutorial.data.database.entity.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.database.entity.UserNameDb
import javax.inject.Inject

class GithubDatabaseSourceImpl @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val userNameDao: UserNameDao
) : GithubDatabaseSource {

    override fun observeRepositoriesByUserName(userName: String): Observable<List<RepositoryDb>> =
        repositoryDao.selectRepositoriesByUserNameDistinct(userName)

    override fun saveRepositories(repositoryDbList: List<RepositoryDb>): Completable =
        repositoryDao.insertRepositories(repositoryDbList)

    override fun saveUserName(userNameDb: UserNameDb): Completable =
        userNameDao.insertUserName(userNameDb)

    override fun getLatestUserName(): Maybe<UserNameDb> =
        userNameDao.selectLatestUserName()
}
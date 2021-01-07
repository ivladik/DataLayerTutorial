package ru.cardsmobile.datalayertutorial.data.source.database

import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.source.database.dto.UserNameDb
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import javax.inject.Inject

class GithubDatabaseSourceImpl @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val userNameDao: UserNameDao
) : GithubDatabaseSource {

    override fun observeRepositoriesByUserName(userName: String): Observable<List<RepositoryDb>> =
        repositoryDao.observeRepositoriesByUserNameDistinct(userName)

    override fun saveRepositories(
        repositoriesDb: List<RepositoryDb>,
        userNameDb: UserNameDb
    ): Completable = userNameDao
        .insertUserName(userNameDb)
        .andThen(repositoryDao.insertRepositories(repositoriesDb))

    override fun getLatestUserName(): Maybe<UserNameDb> =
        userNameDao.selectLatestUserName()
}
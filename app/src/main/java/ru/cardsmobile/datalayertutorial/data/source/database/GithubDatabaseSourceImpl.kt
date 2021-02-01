package ru.cardsmobile.datalayertutorial.data.source.database

import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.source.database.dto.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.source.database.dto.UserNameDb
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import javax.inject.Inject

class GithubDatabaseSourceImpl @Inject constructor(
    private val repositoryDao: RepositoryDao,
    private val userNameDao: UserNameDao,
    private val repositoryUserNameDao: RepositoryUserNameDao
) : GithubDatabaseSource {

    override fun observeRepositoriesByUserName(userName: String): Observable<List<RepositoryDb>> =
        repositoryDao.observeRepositoriesByUserNameDistinct(userName)

    override fun saveRepositories(
        repositoriesDb: List<RepositoryDb>,
        userNameDb: UserNameDb
    ): Completable =
        Completable.fromAction {
            repositoryUserNameDao.saveRepositories(
                userNameDao,
                repositoryDao,
                userNameDb,
                repositoriesDb
            )
        }

    override fun getLatestUserName(): Maybe<UserNameDb> =
        userNameDao.selectLatestUserName()
}
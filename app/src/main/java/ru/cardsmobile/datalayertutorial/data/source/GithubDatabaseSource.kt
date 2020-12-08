package ru.cardsmobile.datalayertutorial.data.source

import io.reactivex.*
import ru.cardsmobile.datalayertutorial.data.database.entity.RepositoryDb
import ru.cardsmobile.datalayertutorial.data.database.entity.UserNameDb

interface GithubDatabaseSource {

    fun observeRepositoriesByUserName(userName: String): Observable<List<RepositoryDb>>

    fun saveRepositories(repositoryDbList: List<RepositoryDb>): Completable

    fun saveUserName(userNameDb: UserNameDb): Completable

    fun getLatestUserName(): Maybe<UserNameDb>
}
package ru.cardsmobile.datalayertutorial.di

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.cardsmobile.datalayertutorial.data.api.GithubApi
import ru.cardsmobile.datalayertutorial.data.database.GithubDatabase
import ru.cardsmobile.datalayertutorial.data.database.dao.RepositoryDao
import ru.cardsmobile.datalayertutorial.data.database.dao.UserNameDao
import ru.cardsmobile.datalayertutorial.data.repository.GithubRepositoryImpl
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSourceImpl
import ru.cardsmobile.datalayertutorial.data.source.GithubNetworkSource
import ru.cardsmobile.datalayertutorial.data.source.GithubNetworkSourceImpl
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import ru.cardsmobile.datalayertutorial.presentation.viewmodel.GithubViewModel
import ru.cardsmobile.datalayertutorial.presentation.viewmodel.ViewModelFactory
import javax.inject.Singleton

@Module
abstract class GithubModule {

    @Binds
    abstract fun bindsGithubRepository(githubRepositoryImpl: GithubRepositoryImpl): GithubRepository

    @Binds
    abstract fun bindsGithubNetworkSource(githubNetworkSourceImpl: GithubNetworkSourceImpl): GithubNetworkSource

    @Binds
    abstract fun bindsGithubDatabaseSource(githubDatabaseSourceImpl: GithubDatabaseSourceImpl): GithubDatabaseSource

    @Binds
    abstract fun bindsViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(GithubViewModel::class)
    abstract fun bindsGithubViewModel(
        viewModel: GithubViewModel
    ): ViewModel

    @Module
    companion object {

        @Provides
        @JvmStatic
        @Singleton
        fun providesGithubDatabase(application: Application): GithubDatabase =
            GithubDatabase.newInstance(application)

        @Provides
        @JvmStatic
        fun providesRepositoryDao(githubDatabase: GithubDatabase): RepositoryDao =
            githubDatabase.repositoryDao()

        @Provides
        @JvmStatic
        fun providesUserNameDao(githubDatabase: GithubDatabase): UserNameDao =
            githubDatabase.userNameDao()

        @Provides
        @JvmStatic
        @Singleton
        fun providesRetrofit(): GithubApi {
            val retrofit = Retrofit
                .Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://api.github.com/")
                .build()

            return retrofit.create(GithubApi::class.java)
        }
    }
}
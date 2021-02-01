package ru.cardsmobile.datalayertutorial.di.module

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
import ru.cardsmobile.datalayertutorial.data.source.network.GithubApi
import ru.cardsmobile.datalayertutorial.data.repository.GithubRepositoryImpl
import ru.cardsmobile.datalayertutorial.data.source.GithubDatabaseSource
import ru.cardsmobile.datalayertutorial.data.source.NetworkSource
import ru.cardsmobile.datalayertutorial.data.source.database.*
import ru.cardsmobile.datalayertutorial.data.source.network.GithubNetworkSourceImpl
import ru.cardsmobile.datalayertutorial.data.source.network.dto.RepositoryDto
import ru.cardsmobile.datalayertutorial.di.ViewModelKey
import ru.cardsmobile.datalayertutorial.domain.repository.GithubRepository
import ru.cardsmobile.datalayertutorial.presentation.ui.main.GithubViewModel
import ru.cardsmobile.datalayertutorial.presentation.ui.base.ViewModelFactory
import javax.inject.Singleton

@Module
abstract class GithubModule {

    @Binds
    abstract fun bindsGithubRepository(githubRepositoryImpl: GithubRepositoryImpl): GithubRepository

    @Binds
    abstract fun bindsGithubNetworkSource(githubNetworkSourceImpl: GithubNetworkSourceImpl): NetworkSource<String, List<RepositoryDto>>

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
        fun providesRepositoryUserNameDao(githubDatabase: GithubDatabase): RepositoryUserNameDao =
            githubDatabase.repositoryUserNameDao()

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
package ru.cardsmobile.datalayertutorial.di.component

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.cardsmobile.datalayertutorial.di.module.GithubModule
import ru.cardsmobile.datalayertutorial.presentation.ui.main.MainActivity
import javax.inject.Singleton

@Component(modules = [GithubModule::class])
@Singleton
interface ApplicationComponent {

    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {

        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}
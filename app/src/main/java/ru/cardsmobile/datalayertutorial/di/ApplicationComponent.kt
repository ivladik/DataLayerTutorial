package ru.cardsmobile.datalayertutorial.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import ru.cardsmobile.datalayertutorial.DataLayerTutorialApplication
import ru.cardsmobile.datalayertutorial.presentation.ui.activity.MainActivity
import javax.inject.Singleton

@Component(modules = [TestModule::class])
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
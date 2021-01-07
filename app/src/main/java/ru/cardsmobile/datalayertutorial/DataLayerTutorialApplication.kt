package ru.cardsmobile.datalayertutorial

import android.app.Application
import ru.cardsmobile.datalayertutorial.di.AppInjector
import ru.cardsmobile.datalayertutorial.di.component.DaggerApplicationComponent

class DataLayerTutorialApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        AppInjector.component = DaggerApplicationComponent
            .factory()
            .create(this)
    }
}
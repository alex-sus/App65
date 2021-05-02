package ru.yodata.app65.di.app

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.library.data.ContactRepository
import javax.inject.Singleton

@Module
class AppModule(private val application: Application) {
    @Singleton
    @Provides
    fun provideApplicationContext(): Context = application

    @Singleton
    @Provides
    fun provideContactRepository(): ContactRepositoryInterface =
            ContactRepository(application.contentResolver)

}
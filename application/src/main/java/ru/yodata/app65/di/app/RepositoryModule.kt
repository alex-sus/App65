package ru.yodata.app65.di.app

import android.content.ContentResolver
import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.library.data.repository.ContactLocationRepository
import ru.yodata.library.data.repository.ContactRepository
import ru.yodata.library.data.room.database.ContactLocationDatabase
import javax.inject.Singleton

@Module
class RepositoryModule {

    @Singleton
    @Provides
    fun provideContactRepository(contentResolver: ContentResolver): ContactRepositoryInterface =
            ContactRepository(contentResolver)

    @Singleton
    @Provides
    fun provideContactLocationRepository(
            db: ContactLocationDatabase,
            contactRepositoryInterface: ContactRepositoryInterface
    ): ContactLocationRepositoryInterface =
            ContactLocationRepository(
                    db,
                    contactRepositoryInterface
            )

}
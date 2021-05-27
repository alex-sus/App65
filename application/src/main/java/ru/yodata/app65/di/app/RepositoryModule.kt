package ru.yodata.app65.di.app

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.java.interactors.GoogleDirectionsRepositoryInterface
import ru.yodata.java.interactors.YandexGeocoderRepositoryInterface
import ru.yodata.library.data.api.GoogleDirectionsApi
import ru.yodata.library.data.api.YandexGeocoderApi
import ru.yodata.library.data.repository.ContactLocationRepository
import ru.yodata.library.data.repository.ContactRepository
import ru.yodata.library.data.repository.GoogleDirectionsRepository
import ru.yodata.library.data.repository.YandexGeocoderRepository
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

    @Singleton
    @Provides
    fun provideYandexGeocoderRepository(
        api: YandexGeocoderApi,
        appContext: Context
    ): YandexGeocoderRepositoryInterface = YandexGeocoderRepository(api, appContext)

    @Singleton
    @Provides
    fun provideGoogleDirectionsRepository(api: GoogleDirectionsApi)
            : GoogleDirectionsRepositoryInterface = GoogleDirectionsRepository(api)

}
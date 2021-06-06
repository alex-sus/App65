package ru.yodata.app65.di.app

import android.content.ContentResolver
import android.content.Context
import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.*
import ru.yodata.library.data.api.GoogleDirectionsApi
import ru.yodata.library.data.api.YandexGeocoderApi
import ru.yodata.library.data.repository.*
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

    @Singleton
    @Provides
    fun provideBirthdayAlarmRepository(
        appContext: Context
    ): BirthdayAlarmRepositoryInterface = BirthdayAlarmRepository(appContext)

    @Singleton
    @Provides
    fun provideAlarmCalendarRepository(): AlarmCalendarRepositoryInterface =
        AlarmCalendarRepository()

}
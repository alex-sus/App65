package ru.yodata.app65.di.app

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.yodata.library.data.room.database.ContactLocationDatabase
import javax.inject.Singleton

@Module
class ContactLocationDatabaseModule {

    @Singleton
    @Provides
    fun provideContactLocationDatabase(context: Context): ContactLocationDatabase =
            Room.databaseBuilder(
                    context,
                    ContactLocationDatabase::class.java, "ContactLocationDb"
            ).build()
}
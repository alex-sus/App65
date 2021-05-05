package ru.yodata.library.data.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.yodata.library.data.room.dao.ContactLocationDao
import ru.yodata.library.data.room.entity.ContactLocationEntity

@Database(entities = [ContactLocationEntity::class], version = 1)
abstract class ContactLocationDatabase : RoomDatabase() {
    abstract fun contactLocationDao(): ContactLocationDao
}
package ru.yodata.library.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact_locations_table")
data class ContactLocationEntity(
        @PrimaryKey
        val contactId: String,
        val latitude: Double,
        val longitude: Double,
        val address: String
)
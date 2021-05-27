package ru.yodata.library.data.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.yodata.java.entities.LocatedContact

@Entity(tableName = "contact_locations_table")
data class ContactLocationEntity(
        @PrimaryKey
        val contactId: String,
        val latitude: Double,
        val longitude: Double,
        val address: String
)

fun LocatedContact.toDatabase() =
        ContactLocationEntity(
                contactId = id,
                latitude = latitude,
                longitude = longitude,
                address = address
        )
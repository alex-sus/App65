package ru.yodata.java.interactors

import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.entities.LocationData

interface ContactLocationRepositoryInterface {

    suspend fun getLocationDataById(contactId: String): LocationData?

    suspend fun getLocatedContactList(): List<LocatedContact>?

    suspend fun addContactLocation(locatedContact: LocatedContact)

    suspend fun updateContactLocation(locatedContact: LocatedContact)
}
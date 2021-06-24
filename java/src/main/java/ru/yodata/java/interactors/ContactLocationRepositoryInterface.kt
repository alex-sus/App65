package ru.yodata.java.interactors

import kotlinx.coroutines.flow.Flow
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.entities.LocationData

interface ContactLocationRepositoryInterface {

    suspend fun getLocationDataById(contactId: String): Flow<LocationData?>

    suspend fun getLocatedContactList(): List<LocatedContact>?

    suspend fun addContactLocation(locatedContact: LocatedContact)

    suspend fun updateContactLocation(locatedContact: LocatedContact)

    suspend fun deleteLocationDataById(contactId: String)
}
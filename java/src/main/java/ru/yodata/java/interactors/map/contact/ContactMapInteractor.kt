package ru.yodata.java.interactors.map.contact

import ru.yodata.java.entities.BriefContact
import ru.yodata.java.entities.LocatedContact

interface ContactMapInteractor {

    suspend fun getLocatedContactList(): List<LocatedContact>?

    suspend fun addContactLocation(locatedContact: LocatedContact)

    suspend fun updateContactLocation(locatedContact: LocatedContact)

    suspend fun getBriefContactById(contactId: String): BriefContact?

    suspend fun reverseGeocoding(
            latitude: Double,
            longitude: Double,
            apikey: String
    ): String
}
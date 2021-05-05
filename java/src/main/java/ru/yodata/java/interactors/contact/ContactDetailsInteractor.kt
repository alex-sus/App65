package ru.yodata.java.interactors.contact

import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData

interface ContactDetailsInteractor {

    suspend fun getContactById(contactId: String): Contact

    suspend fun getLocationDataById(contactId: String): LocationData?
}
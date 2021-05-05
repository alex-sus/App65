package ru.yodata.java.interactors.contact

import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface

class ContactDetailsModel(
        private val contactRepository: ContactRepositoryInterface,
        private val locationRepository: ContactLocationRepositoryInterface
) : ContactDetailsInteractor {

    override suspend fun getContactById(contactId: String): Contact =
            contactRepository.getContactById(contactId)

    override suspend fun getLocationDataById(contactId: String): LocationData? =
            locationRepository.getLocationDataById(contactId)
}
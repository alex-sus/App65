package ru.yodata.java.interactors.map

import ru.yodata.java.entities.BriefContact
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.java.interactors.YandexGeocoderRepositoryInterface

class ContactMapModel(
        private val contactRepository: ContactRepositoryInterface,
        private val locationRepository: ContactLocationRepositoryInterface,
        private val geocoderRepository: YandexGeocoderRepositoryInterface
) : ContactMapInteractor {

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
            locationRepository.getLocatedContactList()

    override suspend fun addContactLocation(locatedContact: LocatedContact) =
            locationRepository.addContactLocation(locatedContact)

    override suspend fun updateContactLocation(locatedContact: LocatedContact) =
            locationRepository.updateContactLocation(locatedContact)

    override suspend fun getBriefContactById(contactId: String): BriefContact? =
            contactRepository.getBriefContactById(contactId)

    override suspend fun reverseGeocoding(
            latitude: Double,
            longitude: Double,
            apikey: String
    ): String =
            geocoderRepository.reverseGeocoding(
                    latitude = latitude,
                    longitude = longitude,
                    apikey = apikey
            )

}
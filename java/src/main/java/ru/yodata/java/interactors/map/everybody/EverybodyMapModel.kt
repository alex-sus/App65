package ru.yodata.java.interactors.map.everybody

import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.interactors.ContactLocationRepositoryInterface

class EverybodyMapModel(
        private val locationRepository: ContactLocationRepositoryInterface
) : EverybodyMapInteractor {

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
            locationRepository.getLocatedContactList()
}
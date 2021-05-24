package ru.yodata.java.interactors.map.route

import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.interactors.ContactLocationRepositoryInterface

class RouteMapModel(
        private val locationRepository: ContactLocationRepositoryInterface
) : RouteMapInteractor {

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
            locationRepository.getLocatedContactList()
}
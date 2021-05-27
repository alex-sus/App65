package ru.yodata.java.interactors.map.route

import ru.yodata.java.entities.ContactRouteDetails
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.GoogleDirectionsRepositoryInterface

class RouteMapModel(
    private val locationRepository: ContactLocationRepositoryInterface,
    private val routeRepository: GoogleDirectionsRepositoryInterface
) : RouteMapInteractor {

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
        locationRepository.getLocatedContactList()

    override suspend fun getContactRouteDetails(
        positionLatitude: Double,
        positionLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        mode: String,
        key: String
    ): ContactRouteDetails? =
        routeRepository.getContactRouteDetails(
            positionLatitude = positionLatitude,
            positionLongitude = positionLongitude,
            destinationLatitude = destinationLatitude,
            destinationLongitude = destinationLongitude,
            mode = mode,
            key = key
        )
}
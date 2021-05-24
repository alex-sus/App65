package ru.yodata.java.interactors.map.route

import ru.yodata.java.entities.ContactRouteDetails
import ru.yodata.java.entities.LocatedContact

interface RouteMapInteractor {

    suspend fun getLocatedContactList(): List<LocatedContact>?

    suspend fun getContactRouteDetails(
        positionLatitude: Double,
        positionLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        mode: String,
        key: String
    ): ContactRouteDetails?
}
package ru.yodata.java.interactors

import ru.yodata.java.entities.ContactRouteDetails

interface GoogleDirectionsRepositoryInterface {

    suspend fun getContactRouteDetails(
        positionLatitude: Double,
        positionLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        mode: String,
        key: String
    ): ContactRouteDetails?
}
package ru.yodata.library.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.yodata.java.entities.ContactRouteDetails
import ru.yodata.java.interactors.GoogleDirectionsRepositoryInterface
import ru.yodata.library.data.api.GoogleDirectionsApi
import ru.yodata.library.utils.Constants.TAG

const val OK_RESPONCE_STATUS = "OK"

class GoogleDirectionsRepository(
    private val api: GoogleDirectionsApi
) : GoogleDirectionsRepositoryInterface {

    override suspend fun getContactRouteDetails(
        positionLatitude: Double,
        positionLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        mode: String,
        key: String
    ): ContactRouteDetails? {
        val responce = withContext(Dispatchers.IO) {
            api.getGoogleDirectionsResponce(
                position = "$positionLatitude,$positionLongitude",
                destination = "$destinationLatitude,$destinationLongitude",
                mode = mode,
                key = key
            )
        }
        return if (responce.status == OK_RESPONCE_STATUS) {
            if (responce.routes.isNotEmpty()) {
                var distance: Int = 0
                with(responce.routes[0]) {
                    legs.forEach { distance = distance + it.distance.value }
                    ContactRouteDetails(
                            encodedRoutePolyline = overviewPolyline.points,
                            routeDistance = distance,
                            northeastLatitude = bounds.northeast.lat,
                            northeastLongitude = bounds.northeast.lng,
                            southwestLatitude = bounds.southwest.lat,
                            southwestLongitude = bounds.southwest.lng
                    )
                }
            } else null
        } else {
            Log.d(TAG, "GoogleDirectionsRepository маршрут не получен: ${responce.status}")
            Log.d(TAG, "Причина: ${responce}")
            null
        }
    }
}
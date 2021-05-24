package ru.yodata.java.entities

data class ContactRouteDetails(
        val encodedRoutePolyline: String,
        val routeDistance: Int,
        val northeastLatitude: Double,
        val northeastLongitude: Double,
        val southwestLatitude: Double,
        val southwestLongitude: Double,
)

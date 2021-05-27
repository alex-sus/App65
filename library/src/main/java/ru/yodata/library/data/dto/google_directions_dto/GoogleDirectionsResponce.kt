package ru.yodata.library.data.dto.google_directions_dto


import com.google.gson.annotations.SerializedName

data class GoogleDirectionsResponce(
    @SerializedName("geocoded_waypoints")
    val geocodedWaypoints: List<GeocodedWaypoint>,
    @SerializedName("routes")
    val routes: List<Route>,
    @SerializedName("status")
    val status: String
)
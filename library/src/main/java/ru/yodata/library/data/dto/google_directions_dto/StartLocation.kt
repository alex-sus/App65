package ru.yodata.library.data.dto.google_directions_dto


import com.google.gson.annotations.SerializedName

data class StartLocation(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lng")
    val lng: Double
)
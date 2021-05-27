package ru.yodata.library.data.dto.google_directions_dto


import com.google.gson.annotations.SerializedName

data class OverviewPolyline(
    @SerializedName("points")
    val points: String
)
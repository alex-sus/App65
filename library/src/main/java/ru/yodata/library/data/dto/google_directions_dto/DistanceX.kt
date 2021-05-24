package ru.yodata.library.data.dto.google_directions_dto


import com.google.gson.annotations.SerializedName

data class DistanceX(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Int
)
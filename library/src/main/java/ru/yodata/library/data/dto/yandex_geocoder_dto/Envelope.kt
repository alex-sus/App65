package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class Envelope(
        @SerializedName("lowerCorner")
        val lowerCorner: String,
        @SerializedName("upperCorner")
        val upperCorner: String
)
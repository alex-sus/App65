package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class Thoroughfare(
        @SerializedName("Premise")
        val premise: Premise,
        @SerializedName("ThoroughfareName")
        val thoroughfareName: String
)
package ru.yodata.library.data.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class AdministrativeArea(
        @SerializedName("AdministrativeAreaName")
        val administrativeAreaName: String,
        @SerializedName("Locality")
        val locality: Locality
)
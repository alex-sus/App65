package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class AdministrativeArea(
        @SerializedName("AdministrativeAreaName")
        val administrativeAreaName: String,
        @SerializedName("Locality")
        val locality: Locality
)
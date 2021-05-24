package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class Country(
        @SerializedName("AddressLine")
        val addressLine: String,
        @SerializedName("AdministrativeArea")
        val administrativeArea: AdministrativeArea,
        @SerializedName("CountryName")
        val countryName: String,
        @SerializedName("CountryNameCode")
        val countryNameCode: String
)
package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class PostalCode(
        @SerializedName("PostalCodeNumber")
        val postalCodeNumber: String
)
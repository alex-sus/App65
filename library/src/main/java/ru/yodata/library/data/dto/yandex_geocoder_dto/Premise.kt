package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class Premise(
        @SerializedName("PostalCode")
        val postalCode: PostalCode,
        @SerializedName("PremiseNumber")
        val premiseNumber: String
)
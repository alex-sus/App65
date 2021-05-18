package ru.yodata.library.data.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class Address(
        @SerializedName("Components")
        val components: List<Component>,
        @SerializedName("country_code")
        val countryCode: String,
        @SerializedName("formatted")
        val formatted: String,
        @SerializedName("postal_code")
        val postalCode: String
)
package ru.yodata.library.data.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class AddressDetails(
        @SerializedName("Country")
        val country: Country
)
package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class BoundedBy(
        @SerializedName("Envelope")
        val envelope: Envelope
)
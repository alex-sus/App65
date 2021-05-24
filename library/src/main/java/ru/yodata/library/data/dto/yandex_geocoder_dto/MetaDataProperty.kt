package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class MetaDataProperty(
        @SerializedName("GeocoderMetaData")
        val geocoderMetaData: GeocoderMetaData
)
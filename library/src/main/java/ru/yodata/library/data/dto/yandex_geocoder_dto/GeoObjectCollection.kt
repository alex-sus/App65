package ru.yodata.library.data.dto.yandex_geocoder_dto


import com.google.gson.annotations.SerializedName

data class GeoObjectCollection(
        @SerializedName("featureMember")
        val featureMember: List<FeatureMember>,
        @SerializedName("metaDataProperty")
        val metaDataProperty: MetaDataPropertyX
)
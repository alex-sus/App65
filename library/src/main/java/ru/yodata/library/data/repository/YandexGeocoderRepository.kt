package ru.yodata.library.data.repository

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.yodata.java.interactors.YandexGeocoderRepositoryInterface
import ru.yodata.library.R
import ru.yodata.library.data.api.YandexGeocoderApi

class YandexGeocoderRepository(
        private val api: YandexGeocoderApi,
        private val appContext: Context
) : YandexGeocoderRepositoryInterface {

    override suspend fun reverseGeocoding(
            latitude: Double,
            longitude: Double,
            apikey: String
    ): String {
        val featureMemberList = withContext(Dispatchers.IO) {
            api.reverseGeocoding(
                    geocode = "$longitude,$latitude",
                    apikey = apikey
            ).response.geoObjectCollection.featureMember
        }
        return if (featureMemberList.isNotEmpty()) {
            featureMemberList[0].geoObject.metaDataProperty.geocoderMetaData.address.formatted
        } else appContext.getString(R.string.address_not_defined_msg)
    }
}
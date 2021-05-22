package ru.yodata.java.interactors

interface YandexGeocoderRepositoryInterface {

    suspend fun reverseGeocoding(
            latitude: Double,
            longitude: Double,
            apikey: String
    ): String

}
package ru.yodata.library.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.yodata.library.data.dto.yandex_geocoder_dto.YandexGeocoderResponse

interface YandexGeocoderApi {

    @GET("1.x/?format=json&results=1&")
    suspend fun reverseGeocoding(
            @Query("geocode") geocode: String,
            @Query("apikey") apikey: String
    ): YandexGeocoderResponse
}
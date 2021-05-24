package ru.yodata.library.data.api

import retrofit2.http.GET
import retrofit2.http.Query
import ru.yodata.library.data.dto.google_directions_dto.GoogleDirectionsResponce

interface GoogleDirectionsApi {

    @GET("json?")
    suspend fun getGoogleDirectionsResponce(
        @Query("origin") position: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String
    ): GoogleDirectionsResponce
}
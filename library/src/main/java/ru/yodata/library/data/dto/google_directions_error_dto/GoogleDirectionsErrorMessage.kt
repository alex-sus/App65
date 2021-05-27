package ru.yodata.library.data.dto.google_directions_error_dto


import com.google.gson.annotations.SerializedName

data class GoogleDirectionsErrorMessage(
    @SerializedName("error_message")
    val errorMessage: String,
    @SerializedName("routes")
    val routes: List<Any>,
    @SerializedName("status")
    val status: String
)
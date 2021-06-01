package ru.yodata.library.utils

import android.net.Uri

object Constants {

    const val TAG = "App65"
    const val CONTACT_ID = "id"
    const val BIRTHDAY_MESSAGE = "b-msg"
    const val CHANNEL_ID = "ru.yodata.app65" // id канала нотификаций
    const val ANDROID_RESOURCE = "android.resource://"
    const val BASE_YANDEX_GEOCODER_API_URL = "https://geocode-maps.yandex.ru/" // для Retrofit
    const val BASE_GOOGLE_DIRECTIONS_API_URL = "https://maps.googleapis.com/maps/api/directions/"
    const val GEOCODING_IN_PROGRESS_SYMBOL =
        "..." // Значение поля адреса, пока не сработал геокодинг
    const val EMPTY_VALUE = "-"
    lateinit var notificationSound: Uri // звук нотификации

}

enum class MapScreenMode {
    CONTACT,
    ROUTE,
    EVERYBODY
}
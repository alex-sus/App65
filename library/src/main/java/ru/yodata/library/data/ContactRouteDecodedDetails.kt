package ru.yodata.library.data

import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions

data class ContactRouteDecodedDetails(
    val routePolylineOptions: PolylineOptions,
    val distanceString: String,
    val routeBounds: LatLngBounds
)

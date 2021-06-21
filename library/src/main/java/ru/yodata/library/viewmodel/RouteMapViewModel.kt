package ru.yodata.library.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.yodata.java.entities.ContactRouteDetails
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.interactors.map.route.RouteMapInteractor
import ru.yodata.library.R
import ru.yodata.library.data.ContactRouteDecodedDetails
import ru.yodata.library.utils.Constants.TAG
import javax.inject.Inject

const val METERS_TO_KILOMETERS = 0.001

class RouteMapViewModel @Inject constructor(
    private val interactor: RouteMapInteractor,
    appContext: Context
) : ViewModel() {

    private val locatedContactList = MutableLiveData<List<LocatedContact>>()
    private val contactRouteDecodedDetails = MutableLiveData<ContactRouteDecodedDetails?>()
    private val googleMapApiKey = appContext.getString(R.string.google_maps_key)

    init {
        loadLocatedContactList()
        Log.d(TAG, "RouteMapViewModel создана")
    }

    override fun onCleared() {
        Log.d(TAG, "RouteMapViewModel уничтожена")
        super.onCleared()
    }

    fun getLocatedContactList(): LiveData<List<LocatedContact>> = locatedContactList

    fun getContactRouteDecodedDetails(): LiveData<ContactRouteDecodedDetails?> =
        contactRouteDecodedDetails

    fun requestContactRouteDecodedDetails(
        firstContactId: String,
        secondContactId: String,
        mode: String,
    ): LiveData<ContactRouteDecodedDetails?> {
        val firstContact = locatedContactList.value?.first { it.id == firstContactId }
        val secondContact = locatedContactList.value?.first { it.id == secondContactId }
        if (firstContact != null && secondContact != null) {
            loadContactRouteDecodedDetails(
                positionLatitude = firstContact.latitude,
                positionLongitude = firstContact.longitude,
                destinationLatitude = secondContact.latitude,
                destinationLongitude = secondContact.longitude,
                mode
            )
        } else contactRouteDecodedDetails.value = null
        return contactRouteDecodedDetails
    }

    private fun loadLocatedContactList() {
        Log.d(TAG, "RouteMapViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch {
            locatedContactList.value = interactor.getLocatedContactList() ?: emptyList()
            Log.d(TAG, "RouteMapViewModel: координаты контактов получены.")
        }
    }

    private fun loadContactRouteDecodedDetails(
        positionLatitude: Double,
        positionLongitude: Double,
        destinationLatitude: Double,
        destinationLongitude: Double,
        mode: String
    ) {
        viewModelScope.launch {
            contactRouteDecodedDetails.value =
                    decodeRouteDetails(
                            interactor.getContactRouteDetails(
                                    positionLatitude = positionLatitude,
                                    positionLongitude = positionLongitude,
                                    destinationLatitude = destinationLatitude,
                                    destinationLongitude = destinationLongitude,
                                    mode = mode,
                                key = googleMapApiKey
                            )
                    )
        }
    }

    private suspend fun decodeRouteDetails(encodedDetails: ContactRouteDetails?)
            : ContactRouteDecodedDetails? {
        return withContext(Dispatchers.Default) {
            if (encodedDetails != null) {
                with(encodedDetails) {
                    ContactRouteDecodedDetails(
                            routePolylineOptions = PolylineOptions()
                                    .addAll(PolyUtil.decode(encodedRoutePolyline)),
                            distanceString = "${
                                "%.1f".format(METERS_TO_KILOMETERS *
                                        routeDistance)
                            } km",
                            routeBounds = LatLngBounds.Builder()
                                    .include(LatLng(northeastLatitude, northeastLongitude))
                                    .include(LatLng(southwestLatitude, southwestLongitude))
                                    .build()
                    )
                }
            } else null
        }
    }

}
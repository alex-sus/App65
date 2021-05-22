package ru.yodata.library.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import ru.yodata.java.entities.BriefContact
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.entities.LocationData
import ru.yodata.java.interactors.map.ContactMapInteractor
import ru.yodata.library.R
import ru.yodata.library.utils.Constants.TAG
import javax.inject.Inject

class ContactLocationsViewModel @Inject constructor(
        private val interactor: ContactMapInteractor,
        private val appContext: Context
) : ViewModel() {

    private val yandexGeocoderApiKey = appContext.getString(R.string.yandex_geocoder_api_key)
    private val locatedContactList = MutableLiveData<MutableList<LocatedContact>>()
    private val changedLocationData = MutableLiveData<LocationData?>()
    private var briefContact = MutableLiveData<BriefContact?>()

    init {
        loadLocatedContactList()
    }

    override fun onCleared() {
        Log.d(TAG, "ContactLocationsViewModel уничтожена")
        super.onCleared()
    }

    fun getChangedLocationData(): LiveData<LocationData?> = changedLocationData

    fun getChangedLatLngData(): LatLng? {
        val changedLocation = changedLocationData.value
        return if (changedLocation != null) {
            LatLng(changedLocation.latitude, changedLocation.longitude)
        } else null
    }

    fun setChangedLocationData(locationData: LocationData) {
        changedLocationData.value = locationData
        reverseGeocoding(locationData.latitude, locationData.longitude, yandexGeocoderApiKey)
        Log.d(TAG, "ContactLocationsViewModel: изменение положения маркера записано")
    }

    fun resetChangedLocationData() {
        changedLocationData.value = null
    }

    fun getBriefContactById(contactId: String): LiveData<BriefContact?> {
        loadBriefContactById(contactId)
        return briefContact
    }

    fun getLocatedContactList(): LiveData<List<LocatedContact>> =
            locatedContactList as LiveData<List<LocatedContact>>

    /*suspend fun getLocatedContactById(contactId: String): LocatedContact? {
        Log.d(TAG, "ContactLocationsViewModel:getLocatedContactById начинаю обращение к locatedContactList...")
        return locatedContactList.value?.firstOrNull { it.id == contactId }
    }*/

    fun addLocatedContact(locatedContact: LocatedContact) {
        viewModelScope.launch {
            locatedContactList.value?.add(locatedContact)
            interactor.addContactLocation(locatedContact)
        }
    }

    fun updateLocatedContact(locatedContact: LocatedContact) {
        viewModelScope.launch {
            val list = locatedContactList.value
            if (list != null) {
                locatedContactList.value?.set(
                        list.indexOfFirst { it.id == locatedContact.id },
                        locatedContact
                )
                interactor.updateContactLocation(locatedContact)
            }
        }
    }

    private fun reverseGeocoding(
            latitude: Double,
            longitude: Double,
            apikey: String
    ) {
        viewModelScope.launch {
            try {
                changedLocationData.postValue(
                        changedLocationData.value?.copy(
                                address = interactor.reverseGeocoding(
                                        latitude = latitude,
                                        longitude = longitude,
                                        apikey = apikey
                                ))
                )
            } catch (e: Throwable) {
                Log.d(TAG, e.stackTraceToString())
                changedLocationData.postValue(
                        changedLocationData.value?.copy(
                                address = appContext.getString(R.string.address_not_defined_msg))
                )
            }
        }
    }

    fun isNoOneContactHaveCoordinates(): Boolean = locatedContactList.value?.size == 0

    private fun loadLocatedContactList() {
        Log.d(TAG, "ContactLocationsViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch {
            locatedContactList.postValue((interactor.getLocatedContactList() ?: emptyList())
                    as MutableList<LocatedContact>)
            Log.d(TAG, "ContactLocationsViewModel: координаты контактов получены.")
        }
    }

    private fun loadBriefContactById(contactId: String) {
        viewModelScope.launch {
            briefContact.postValue(interactor.getBriefContactById(contactId))
        }
    }
}
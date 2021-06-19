package ru.yodata.library.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.yodata.java.entities.BriefContact
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.entities.LocationData
import ru.yodata.java.interactors.map.contact.ContactMapInteractor
import ru.yodata.library.R
import ru.yodata.library.utils.Constants.TAG
import java.io.IOException
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
class ContactLocationsViewModel @Inject constructor(
    private val interactor: ContactMapInteractor,
    // утечки контекста здесь не будет, т.к. используется Application Context
    private val appContext: Context
) : ViewModel() {

    private val yandexGeocoderApiKey = appContext.getString(R.string.yandex_geocoder_api_key)
    private val locatedContactList = MutableLiveData<MutableList<LocatedContact>>()
    private val changedLocationData = MutableLiveData<LocationData?>()
    private var briefContact = MutableLiveData<BriefContact?>()

    init {
        loadLocatedContactList()
    }

    fun getChangedLocationData(): LiveData<LocationData?> = changedLocationData

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

    @Suppress("UNCHECKED_CAST")
    fun getLocatedContactList(): LiveData<List<LocatedContact>> =
        locatedContactList as LiveData<List<LocatedContact>>

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
                changedLocationData.value =
                    changedLocationData.value?.copy(
                        address = interactor.reverseGeocoding(
                            latitude = latitude,
                            longitude = longitude,
                            apikey = apikey
                        )
                    )
            } catch (e: IOException) {
                Log.d(TAG, e.stackTraceToString())
                changedLocationData.value =
                    changedLocationData.value?.copy(
                        address = appContext.getString(R.string.address_not_defined_msg)
                    )
            }
        }
    }

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
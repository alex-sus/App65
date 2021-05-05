package ru.yodata.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.yodata.java.entities.BriefContact
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.entities.LocationData
import ru.yodata.java.interactors.map.ContactMapInteractor
import ru.yodata.library.utils.Constants.TAG
import javax.inject.Inject

class ContactLocationsViewModel @Inject constructor(
        private val interactor: ContactMapInteractor
) : ViewModel() {

    private val locatedContactList = MutableLiveData<MutableList<LocatedContact>>()
    private val changedLocationData = MutableLiveData<LocationData?>()
    private var briefContact: BriefContact? = null

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
    }

    fun resetChangedLocationData() {
        changedLocationData.value = null
    }

    fun getBriefContactById(contactId: String): BriefContact? {
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

    fun isNoOneContactHaveCoordinates(): Boolean = locatedContactList.value?.size == 0

    private fun loadLocatedContactList() {
        Log.d(TAG, "ContactLocationsViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch(Dispatchers.IO) {
            locatedContactList.postValue((interactor.getLocatedContactList() ?: emptyList())
                    as MutableList<LocatedContact>)
            Log.d(TAG, "ContactLocationsViewModel: координаты контактов получены.")
        }
    }

    private fun loadBriefContactById(contactId: String) {
        runBlocking {
            briefContact = interactor.getBriefContactById(contactId)
        }
    }
}
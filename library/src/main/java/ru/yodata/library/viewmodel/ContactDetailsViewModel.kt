package ru.yodata.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData
import ru.yodata.java.interactors.contact.ContactDetailsInteractor
import ru.yodata.library.utils.Constants
import javax.inject.Inject

class ContactDetailsViewModel @Inject constructor(
        private val interactor: ContactDetailsInteractor
) : ViewModel() {

    private val contact = MutableLiveData<Contact>()
    private val locationData = MutableLiveData<LocationData?>()

    override fun onCleared() {
        Log.d(Constants.TAG, "ContactDetailsViewModel уничтожена")
        
        super.onCleared()
    }

    fun getContactById(contactId: String): LiveData<Contact> {
        if (contactId != contact.value?.id) {
            loadContactDetail(contactId)
        }
        return contact
    }

    fun getLocationDataById(contactId: String): LiveData<LocationData?> {
        loadLocationData(contactId)
        return locationData
    }

    private fun loadContactDetail(contactId: String) {
        viewModelScope.launch {
            interactor.getContactById(contactId).also { contact.postValue(it) }
        }
    }

    private fun loadLocationData(contactId: String) {
        viewModelScope.launch {
            interactor.getLocationDataById(contactId).also { locationData.postValue(it) }
        }
    }

}
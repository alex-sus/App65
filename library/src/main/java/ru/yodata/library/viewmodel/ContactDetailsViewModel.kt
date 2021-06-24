package ru.yodata.library.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.contact.ContactDetailsInteractor
import ru.yodata.library.utils.Constants
import ru.yodata.library.view.contact.ContactDetailsState
import ru.yodata.library.view.contact.emptyState
import javax.inject.Inject

class ContactDetailsViewModel @Inject constructor(
        private val interactor: ContactDetailsInteractor
) : ViewModel() {

    private val state = MutableStateFlow<ContactDetailsState>(emptyState())
    /*private val contact = MutableLiveData<Contact>()
    private val locationData = MutableLiveData<LocationData?>()*/

    override fun onCleared() {
        Log.d(Constants.TAG, "ContactDetailsViewModel уничтожена")
        super.onCleared()
    }

    fun getContactStateById(contactId: String): Flow<ContactDetailsState?> {
        loadContactDetail(contactId)
        loadLocationData(contactId)
        return state
    }

    fun deleteLocationDataById(contactId: String) {
        viewModelScope.launch {
            interactor.deleteLocationDataById(contactId)
        }
        state.value = state.value.copy(
            locationData = null
        )
    }

    fun isBirthdayAlarmOn(curContact: Contact): Boolean =
        interactor.isBirthdayAlarmOn(curContact)

    fun setBirthdayAlarm(curContact: Contact) =
        viewModelScope.launch {
            interactor.setBirthdayAlarm(curContact)
        }

    fun cancelBirthdayAlarm(curContact: Contact) =
        viewModelScope.launch {
            interactor.cancelBirthdayAlarm(curContact)
        }

    private fun loadContactDetail(contactId: String) {
        viewModelScope.launch {
            interactor.getContactById(contactId).collect {
                state.value = state.value.copy(contactDetails = it)
            }
        }
    }

    private fun loadLocationData(contactId: String) {
        viewModelScope.launch {
            interactor.getLocationDataById(contactId).collect {
                state.value = state.value.copy(locationData = it)
            }
        }
    }

}


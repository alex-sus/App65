package ru.yodata.library.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.contact.ContactDetailsInteractor
import javax.inject.Inject

class ContactDetailsViewModel @Inject constructor(
        private val interactor: ContactDetailsInteractor
) : ViewModel() {

    private val contact = MutableLiveData<Contact>()

    fun getContactById(id: String): LiveData<Contact> {
        if (id != contact.value?.id) {
            loadContactDetail(id)
        }
        return contact
    }

    private fun loadContactDetail(contactId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            interactor.getContactById(contactId).also { contact.postValue(it) }
        }
    }

}
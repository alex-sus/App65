package ru.yodata.app65.viewmodel

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.*
import ru.yodata.app65.model.Contact
import ru.yodata.app65.model.ContactRepository

class ContactDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val contResolver = application.contentResolver
    private val contact = MutableLiveData<Contact>()

    fun getContactById(id: String): LiveData<Contact> {
        if (id != contact.value?.id) {
            loadContactDetail(id)
        }
        
        return contact
    }

    private fun loadContactDetail(contactId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            ContactRepository.getContactById(contResolver, contactId).also { contact.postValue(it) }
        }
    }

}
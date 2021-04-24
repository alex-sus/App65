package ru.yodata.app65.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yodata.app65.model.Contact
import ru.yodata.app65.model.ContactRepositoryInterface
import javax.inject.Inject

class ContactDetailViewModel @Inject constructor(
        private val contactRepository: ContactRepositoryInterface
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
            contactRepository.getContactById(contactId).also { contact.postValue(it) }
        }
    }

}
package ru.yodata.app65.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.*
import ru.yodata.app65.model.BriefContact
import ru.yodata.app65.model.ContactRepository
import ru.yodata.app65.utils.Constants.TAG

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val contResolver = application.contentResolver
    private val contactList = MutableLiveData<List<BriefContact>>()

    init {
        loadContactList()
    }

    fun getContactList(): LiveData<List<BriefContact>> {
        return contactList
    }

    private fun loadContactList() {
        Log.d(TAG, "ContactListViewModel начинаю запрос данных контактов...")
        viewModelScope.launch(Dispatchers.IO) {
            ContactRepository.getContactList(contResolver).also { contactList.postValue(it) }
        }
        Log.d(TAG, "ContactListViewModel данные контактов получены.")
    }

}


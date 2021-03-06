package ru.yodata.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yodata.java.entities.BriefContact
import ru.yodata.java.interactors.contacts.ContactListInteractor
import ru.yodata.library.utils.Constants.TAG
import javax.inject.Inject

class ContactListViewModel @Inject constructor(
        private val interactor: ContactListInteractor
) : ViewModel() {
    private lateinit var contactList: List<BriefContact> // полный список контактов
    private val filteredList =
            MutableLiveData<List<BriefContact>>() // фильтрованный список контактов
    var currentFilterValue = " " // фильтр имен, соответствующий filteredList

    init {
        loadContactList()
    }

    fun setNameFilter(newFilterValue: String) {
        // Фильтрация нужна только если старый и новый фильтры отличаются
        if (currentFilterValue != newFilterValue) {
            if (newFilterValue.isEmpty()) {
                filteredList.postValue(contactList)
            } else {
                Log.d(TAG, "setNameFilter фильтрую контакты. Фильтр: $newFilterValue")
                viewModelScope.launch(Dispatchers.Default) {
                    // При фильтрации списка контактов есть разница в случаях, когда пользователь
                    // добавляет буквы в фильтр SearchView и когда стирает их оттуда.
                    // В первом случае можно фильтровать не изначальный полный список контактов
                    // (contactList), а уже отфильтрованный на предыдущем шаге (filteredList) - это
                    // будет гораздо быстрее, т.к. элементов там меньше.
                    // Но если пользователь стирает буквы из ранее набранного фильтра, придется
                    // фильтровать на каждом шаге изначальный список (contactList)
                    if (newFilterValue.contains(currentFilterValue)) {
                        filteredList.postValue(filteredList.value?.filter {
                            it.name.contains(newFilterValue, true)
                        })
                    } else {
                        filteredList.postValue(contactList.filter {
                            it.name.contains(newFilterValue, true)
                        })
                    }
                    currentFilterValue = newFilterValue
                }
            }
        }
    }

    fun getFilteredContactList(): LiveData<List<BriefContact>> = filteredList

    private fun loadContactList() {
        Log.d(TAG, "ContactListViewModel начинаю запрос данных контактов...")
        viewModelScope.launch(Dispatchers.IO) {
            contactList = interactor.getContactList()
            filteredList.postValue(contactList)
            Log.d(TAG, "ContactListViewModel данные контактов получены.")
        }
    }

}


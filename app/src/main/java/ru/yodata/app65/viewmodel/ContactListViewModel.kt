package ru.yodata.app65.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.yodata.app65.model.BriefContact
import ru.yodata.app65.model.ContactRepository
import ru.yodata.app65.utils.Constants.TAG

class ContactListViewModel(application: Application) : AndroidViewModel(application) {

    private val contResolver = application.contentResolver
    private lateinit var contactList: List<BriefContact> // полный список контактов
    private val filteredList =
        MutableLiveData<List<BriefContact>>() // фильтрованный список контактов
    var currentFilterValue = " " // фильтр имен, соответствующий filteredList
    private val isContactListLoading = MutableLiveData(false)
    private val disposable: CompositeDisposable = CompositeDisposable()

    init {
        loadContactList()
    }

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
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

    fun isContactListLoading(): LiveData<Boolean> = isContactListLoading

    private fun loadContactList() {
        Log.d(TAG, "ContactListViewModel начинаю запрос данных контактов...")
        ContactRepository.getContactList(contResolver)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isContactListLoading.postValue(true) }
            .subscribeBy(
                onSuccess = {
                    contactList = it
                    filteredList.postValue(it)
                    isContactListLoading.postValue(false)
                },
                onError = {
                    isContactListLoading.postValue(false)
                    Log.d(TAG, "ContactListViewModel ошибка при загрузке списка контактов:")
                    Log.d(TAG, it.stackTraceToString())
                }
            )
            .addTo(disposable)
        Log.d(TAG, "ContactListViewModel данные контактов получены.")
    }

}


package ru.yodata.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.interactors.map.everybody.EverybodyMapInteractor
import ru.yodata.library.utils.Constants.TAG
import javax.inject.Inject

class EverybodyMapViewModel @Inject constructor(
        private val interactor: EverybodyMapInteractor
) : ViewModel() {

    private val locatedContactList = MutableLiveData<List<LocatedContact>>()

    init {
        loadLocatedContactList()
    }

    override fun onCleared() {
        Log.d(TAG, "EverybodyMapViewModel уничтожена")
        super.onCleared()
    }

    fun getLocatedContactList(): LiveData<List<LocatedContact>> = locatedContactList

    private fun loadLocatedContactList() {
        Log.d(TAG, "EverybodyMapViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch {
            locatedContactList.postValue(interactor.getLocatedContactList() ?: emptyList())
            Log.d(TAG, "EverybodyMapViewModel: координаты контактов получены.")
        }
    }

}
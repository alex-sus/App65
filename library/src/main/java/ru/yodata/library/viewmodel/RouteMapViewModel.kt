package ru.yodata.library.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.interactors.map.route.RouteMapInteractor
import ru.yodata.library.utils.Constants
import ru.yodata.library.utils.Constants.TAG
import javax.inject.Inject

class RouteMapViewModel @Inject constructor(
        private val interactor: RouteMapInteractor
) : ViewModel() {

    private val locatedContactList = MutableLiveData<List<LocatedContact>>()

    init {
        loadLocatedContactList()
        Log.d(TAG, "RouteMapViewModel создана")
    }

    override fun onCleared() {
        Log.d(TAG, "RouteMapViewModel уничтожена")
        super.onCleared()
    }

    fun getLocatedContactList(): LiveData<List<LocatedContact>> = locatedContactList

    private fun loadLocatedContactList() {
        Log.d(TAG, "RouteMapViewModel: начинаю запрос координат контактов...")
        viewModelScope.launch {
            locatedContactList.postValue(interactor.getLocatedContactList() ?: emptyList())
            Log.d(Constants.TAG, "RouteMapViewModel: координаты контактов получены.")
        }
    }

}
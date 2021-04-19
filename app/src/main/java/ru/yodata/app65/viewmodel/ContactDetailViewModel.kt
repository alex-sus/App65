package ru.yodata.app65.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.yodata.app65.model.Contact
import ru.yodata.app65.model.ContactRepository
import ru.yodata.app65.utils.Constants

class ContactDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val contResolver = application.contentResolver
    private val contact = MutableLiveData<Contact>()
    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        disposable.dispose()
        super.onCleared()
    }

    fun getContactById(id: String): LiveData<Contact> {
        if (id != contact.value?.id) {
            loadContactDetail(id)
        }
        return contact
    }

    private fun loadContactDetail(contactId: String) {
        ContactRepository.getContactById(contResolver, contactId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onSuccess = {
                    contact.postValue(it)
                },
                onError = {
                    Log.d(Constants.TAG, "ContactDetailViewModel ошибка при загрузке контакта")
                    Log.d(Constants.TAG, it.stackTraceToString())
                }
            )
            .addTo(disposable)
    }

}
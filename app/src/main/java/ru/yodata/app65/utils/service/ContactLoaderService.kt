package ru.yodata.app65.utils.service

import android.app.Service
import android.content.ContentResolver
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.*
import ru.yodata.app65.model.BriefContact
import ru.yodata.app65.model.Contact
import ru.yodata.app65.model.ContactDataSource
import ru.yodata.app65.utils.Constants
import ru.yodata.app65.utils.Constants.SHOW_EMPTY_VALUE
import ru.yodata.app65.utils.Constants.TAG
import java.util.*

class ContactLoaderService : Service() {

    inner class ContactLoaderBinder : Binder() {
        fun getService(): ContactLoaderService = this@ContactLoaderService
    }

    private val binder = ContactLoaderBinder()

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG, "Bind произошел сейчас:")
        Log.d(TAG, "Старт метода: ${this::class.java.simpleName}:" +
                "${object {}.javaClass.getEnclosingMethod().getName()}")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG, "На самом деле Unbind произошел только сейчас:")
        Log.d(TAG, "Старт метода: ${this::class.java.simpleName}:" +
                "${object {}.javaClass.getEnclosingMethod().getName()}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG, "Сервис уничтожен.")
        super.onDestroy()
    }

    fun getContactList(contResolver: ContentResolver): List<BriefContact> =
            ContactDataSource.getContactList(contResolver)

    fun getContactById(contResolver: ContentResolver, contactId: String): Contact =
            ContactDataSource.getContactById(contResolver, contactId)
}
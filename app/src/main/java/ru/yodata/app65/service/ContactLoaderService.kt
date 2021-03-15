package ru.yodata.app65.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.*
import ru.yodata.app65.utils.Constants.TAG
import ru.yodata.app65.utils.Constants.contactList

class ContactLoaderService : Service() {

    inner class ContactLoaderBinder: Binder() {
        fun getService(): ContactLoaderService = this@ContactLoaderService
    }

    private val binder = ContactLoaderBinder()
    private lateinit var coroutineScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onBind(intent: Intent): IBinder {
        Log.d(TAG,"Bind произошел сейчас:")
        Log.d(TAG,"Старт метода: ${this::class.java.simpleName}:" +
                "${object {}.javaClass.getEnclosingMethod().getName()}")
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(TAG,"На самом деле Unbind произошел только сейчас:")
        Log.d(TAG,"Старт метода: ${this::class.java.simpleName}:" +
                "${object {}.javaClass.getEnclosingMethod().getName()}")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(TAG,"Сервис уничтожен.")
        // Убить все долгие работы по загрузке данных, даже если они еще продолжаются
        coroutineScope.cancel()
        super.onDestroy()
    }

    suspend fun getContactList() =
        withContext(coroutineScope.coroutineContext) {
             delay(2_000)
             contactList
    }

    suspend fun getContactById(contactId: String) =
         withContext(coroutineScope.coroutineContext) {
             delay(2_000)
             contactList.last { it.id == contactId }
    }
}
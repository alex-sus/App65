package ru.yodata.app65.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import ru.yodata.app65.R
import ru.yodata.app65.model.Contact
import ru.yodata.app65.service.ContactLoaderService
import ru.yodata.app65.service.OnContactLoaderServiceCallback
import ru.yodata.app65.utils.Constants.TAG

class MainActivity : AppCompatActivity(), OnContactListCallback, OnContactLoaderServiceCallback {

    private lateinit var contactLoaderService: ContactLoaderService
    private var firstAppStart = false
    private var serviceBound = false
    private val connection = object: ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            Log.d(TAG,"Старт метода: ${this::class.java.simpleName}:" +
                    "${object {}.javaClass.getEnclosingMethod().getName()}")
            Log.d(TAG,"Только сейчас в MainActivity можно получить ссылку на ibinder")
            contactLoaderService = (service as ContactLoaderService.ContactLoaderBinder).getService()
            serviceBound = true
            if (firstAppStart) navigateToContactListFragment()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (!serviceBound) {
            Log.d(TAG,"Старт метода: ${this::class.java.simpleName}:" +
                    "${object {}.javaClass.getEnclosingMethod().getName()}")
            //Log.d(TAG,"Присоединяюсь к сервису в onCreate...")
            val intent = Intent(this, ContactLoaderService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        if (savedInstanceState == null) {
            firstAppStart = true
        }
    }

    private fun navigateToContactListFragment() {
        supportFragmentManager.beginTransaction()
                .replace(
                        R.id.frag_container,
                        ContactListFragment()
                )
                .commit()
    }

    override fun navigateToContactDetailsFragment(contactId: String) {
        supportFragmentManager.beginTransaction()
                .replace(
                        R.id.frag_container,
                        ContactDetailsFragment.newInstance(contactId),
                        ContactDetailsFragment.FRAGMENT_NAME
                )
                .addToBackStack(ContactDetailsFragment.FRAGMENT_NAME)
                .commit()
    }

    override fun isServiceBound(): Boolean = serviceBound

    override suspend fun getContactList(): List<Contact> {
        return contactLoaderService.getContactList()
    }

    override suspend fun getContactById(contactId: String): Contact {
        return contactLoaderService.getContactById(contactId)
    }

    override fun onDestroy() {
        if (serviceBound) {
            Log.d(TAG,"--Произошло изменение конфигурации или выход из приложения---")
            Log.d(TAG,"Старт метода: ${this::class.java.simpleName}:" +
                    "${object {}.javaClass.getEnclosingMethod().getName()}")
            Log.d(TAG,"Даю команду unbindService(connection). Произошел ли сейчас Unbind?")
            unbindService(connection)
            serviceBound = false
        }
        super.onDestroy()
    }
}
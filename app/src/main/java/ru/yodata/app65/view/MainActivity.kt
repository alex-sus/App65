package ru.yodata.app65.view

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import ru.yodata.app65.R
import ru.yodata.app65.model.Contact
import ru.yodata.app65.utils.service.ContactLoaderService
import ru.yodata.app65.utils.service.OnContactLoaderServiceCallback
import ru.yodata.app65.utils.Constants.CONTACT_ID
import ru.yodata.app65.utils.Constants.TAG

// Значение интента при старте активити по ярлыку на экране
private const val LAUNCHER_START_INTENT = "android.intent.action.MAIN"

class MainActivity : AppCompatActivity(), OnContactListCallback, OnContactLoaderServiceCallback {

    private lateinit var contactLoaderService: ContactLoaderService
    //private val curIntent by lazy(LazyThreadSafetyMode.NONE) {intent}
    private val curIntent by lazy(LazyThreadSafetyMode.NONE) { intent }
    private val activityStartedByNotification by lazy(LazyThreadSafetyMode.NONE) {
        curIntent.action != LAUNCHER_START_INTENT }
    private var firstAppStart = false
    private var serviceBound = false

    private val connection = object: ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d(TAG,"Старт метода: ${this::class.java.simpleName}:" +
                    "${object {}.javaClass.getEnclosingMethod().getName()}")
            Log.d(TAG,"Только сейчас в MainActivity можно получить ссылку на ibinder")
            contactLoaderService = (service as ContactLoaderService.ContactLoaderBinder).getService()
            serviceBound = true
            if (firstAppStart && !activityStartedByNotification) navigateToContactListFragment()
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
            val intent = Intent(this, ContactLoaderService::class.java)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
        if (savedInstanceState == null) {
            firstAppStart = true
            // Если MainActivity стартовало по notification, перейти сразу на экран деталей контакта
            val contactId = curIntent.getStringExtra(CONTACT_ID)
            if (activityStartedByNotification && !contactId.isNullOrEmpty()) {
                navigateToContactDetailsFragment(contactId)
            }
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
        val transaction = supportFragmentManager.beginTransaction()
                .replace(
                        R.id.frag_container,
                        ContactDetailsFragment.newInstance(contactId),
                        ContactDetailsFragment.FRAGMENT_NAME
                )
        if (!activityStartedByNotification)
            transaction.addToBackStack(ContactDetailsFragment.FRAGMENT_NAME)
        transaction.commit()
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
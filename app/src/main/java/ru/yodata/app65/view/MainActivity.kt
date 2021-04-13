package ru.yodata.app65.view

import android.Manifest
import android.content.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import ru.yodata.app65.R
import ru.yodata.app65.utils.Constants
//import ru.yodata.app65.utils.service.ContactLoaderService
//import ru.yodata.app65.utils.service.OnContactLoaderServiceCallback
import ru.yodata.app65.utils.Constants.CONTACT_ID
import ru.yodata.app65.utils.Constants.TAG
import ru.yodata.app65.utils.PermissionsAccessHelper.isPermissionsRequestSuccessful
import ru.yodata.app65.utils.PermissionsAccessHelper.startPermissionsRequest

// Значение интента при старте активити по ярлыку на экране
private const val LAUNCHER_START_INTENT = "android.intent.action.MAIN"

class MainActivity : AppCompatActivity(), OnContactListCallback {

    val askPermissions = arrayOf(
            Manifest.permission.READ_CONTACTS
    )
    private val curIntent by lazy(LazyThreadSafetyMode.NONE) { intent }
    private val activityStartedByNotification by lazy(LazyThreadSafetyMode.NONE) {
        curIntent.action != LAUNCHER_START_INTENT
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (savedInstanceState == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                startPermissionsRequest(
                    activity = this,
                    permissions = askPermissions,
                    showIntro = true
                )
            }
            else chooseNavigation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Проверить даны ли запрошенные ранее разрешения пользователем
        if (isPermissionsRequestSuccessful(
                        activity = this,
                        showEpilogue = true
            )
        ) {
            chooseNavigation()
        }
    }

    private fun chooseNavigation() {
        if (!activityStartedByNotification) {
            navigateToContactListFragment()
        }
        else {
            val contactId = curIntent.getStringExtra(CONTACT_ID)
            if (!contactId.isNullOrEmpty()) {
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

}
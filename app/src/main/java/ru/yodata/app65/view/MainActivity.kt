package ru.yodata.app65.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ru.yodata.app65.R

class MainActivity : AppCompatActivity(), OnContactListCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        if (savedInstanceState == null) {
            navigateToContactListFragment()
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
}
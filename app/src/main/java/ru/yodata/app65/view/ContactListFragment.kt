package ru.yodata.app65.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import kotlinx.coroutines.*
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactListBinding
import ru.yodata.app65.model.BriefContact
import ru.yodata.app65.utils.Constants.TAG
import ru.yodata.app65.viewmodel.ContactListViewModel

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {

    private var listFrag: FragmentContactListBinding? = null

    private var navigateCallback: OnContactListCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactListCallback) {
            navigateCallback = context
        } else throw ClassCastException(context.toString() +
                                        " must implement OnContactListCallback!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFrag = FragmentContactListBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                                                getString(R.string.contact_list_fragment_title)
        val contactListViewModel: ContactListViewModel by viewModels()
        contactListViewModel.getContactList().observe(viewLifecycleOwner, { contactList ->
            Log.d(TAG, "ContactListFragment обзервер сработал")
            if (!contactList.isNullOrEmpty()) {
                val curContact = contactList[0] // <- Это временно, пока нет Recycler View
                try {
                    showContactBrief(curContact)
                    view.setOnClickListener {
                        navigateCallback?.navigateToContactDetailsFragment(curContact.id)
                    }
                } catch (e: IllegalStateException) {
                    Log.d(TAG, "Исключение в ContactListFragment: ")
                    Log.d(TAG, e.stackTraceToString())
                }
            }
        })
    }

    private fun showContactBrief(curContact: BriefContact) {
        with(curContact) {
            listFrag?.apply {
                nameTv.text = name
                phoneTv.text = phone
            }
        }
    }

    override fun onDestroyView() {
        listFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        navigateCallback = null
        super.onDetach()
    }
}
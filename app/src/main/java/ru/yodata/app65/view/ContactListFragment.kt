package ru.yodata.app65.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactListBinding
import ru.yodata.app65.model.Contact
import ru.yodata.app65.utils.service.OnContactLoaderServiceCallback
import ru.yodata.app65.utils.Constants.TAG

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {

    private var listFrag: FragmentContactListBinding? = null

    private var contactId: String = "1"
    private var navigateCallback: OnContactListCallback? = null
    private var loaderCallback: OnContactLoaderServiceCallback? = null
    private lateinit var coroutineScope: CoroutineScope

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactListCallback) {
            navigateCallback = context
        } else throw ClassCastException(context.toString() +
                                        " must implement OnContactListCallback!")
        if (context is OnContactLoaderServiceCallback) {
            loaderCallback = context
        } else throw ClassCastException(context.toString() +
                " must implement OnContactLoaderServiceCallback!")
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFrag = FragmentContactListBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                                                getString(R.string.contact_list_fragment_title)
        loaderCallback?.run {
            coroutineScope.launch {
                while (!isServiceBound()) {}
                val curContact = getContactList()[0]
                try {
                    requireActivity().runOnUiThread {
                        showContactBrief(curContact)
                    }
                }
                catch (e: IllegalStateException) {
                    Log.d(TAG,"Исключение в ContactListFragment: ")
                    Log.d(TAG, e.stackTraceToString())
                }
            }
        }
        view.setOnClickListener { navigateCallback?.navigateToContactDetailsFragment(contactId) }
    }

    private fun showContactBrief(curContact: Contact) {
        with(curContact) {
            listFrag?.apply {
                nameTv.text = name
                phoneTv.text = phone1
            }
        }
    }

    override fun onDestroyView() {
        listFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        navigateCallback = null
        loaderCallback = null
        // Убить все долгие работы по загрузке данных, даже если они еще продолжаются
        coroutineScope.cancel()
        super.onDetach()
    }
}
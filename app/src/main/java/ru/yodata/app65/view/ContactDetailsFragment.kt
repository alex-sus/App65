package ru.yodata.app65.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactDetailsBinding
import ru.yodata.app65.model.Contact
import ru.yodata.app65.service.OnContactLoaderServiceCallback
import ru.yodata.app65.utils.Constants
import ru.yodata.app65.utils.Constants.TAG

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {
    private var detailsFrag: FragmentContactDetailsBinding? = null

    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }
    private var loaderCallback: OnContactLoaderServiceCallback? = null
    private lateinit var coroutineScope: CoroutineScope

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactLoaderServiceCallback) {
            loaderCallback = context
        } else throw ClassCastException(context.toString() +
                " must implement OnContactLoaderServiceCallback!")
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsFrag = FragmentContactDetailsBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                                            getString(R.string.contact_details_fragment_title)
        loaderCallback?.run {
            coroutineScope.launch {
                while (!isServiceBound()) {}
                val curContact = getContactById(contactId)
                try {
                    requireActivity().runOnUiThread {
                        showContactDetails(curContact)
                    }
                }
                catch (e: IllegalStateException) {
                    Log.d(TAG,"Исключение в ContactDetailsFragment: ")
                    Log.d(TAG, e.stackTraceToString())
                }
            }
        }
    }

    private fun showContactDetails(curContact: Contact) {
        with(curContact) {
            detailsFrag?.apply {
                fullNameTv.text = name
                phone1Tv.text = phone1
                phone2Tv.text = phone2
                email1Tv.text = email1
                email2Tv.text = email2
                descriptionTv.text = description
            }
        }
    }

    override fun onDestroyView() {
        detailsFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        Log.d(Constants.TAG,"Старт метода: ${this::class.java.simpleName}:" +
                "${object {}.javaClass.getEnclosingMethod().getName()}")
        loaderCallback = null
        // Убить все долгие работы по загрузке данных, даже если они еще продолжаются
        coroutineScope.cancel()
        super.onDetach()
    }

    companion object {
        private const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = ContactDetailsFragment::class.java.name
        @JvmStatic
        fun newInstance(contactId: String) =
            ContactDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(CONTACT_ID, contactId)
                }
            }
    }
}
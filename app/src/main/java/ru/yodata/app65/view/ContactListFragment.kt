package ru.yodata.app65.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactListBinding
import ru.yodata.app65.utils.Constants.contactList

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {
    private var listFrag: FragmentContactListBinding? = null

    private var contactId: String = "1"
    private var mCallback: OnContactListCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactListCallback) {
            mCallback = context
        } else throw ClassCastException(context.toString() +
                                        " must implement OnContactListCallback!")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listFrag = FragmentContactListBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                                                getString(R.string.contact_list_fragment_title)
        val curContact = contactList.last { it.id == contactId }
        with (curContact) {
            listFrag?.apply {
                nameTv.text = name
                phoneTv.text = phone1
            }
        }
        view.setOnClickListener { mCallback?.navigateToContactDetailsFragment(contactId) }
    }

    override fun onDestroyView() {
        listFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        mCallback = null
        super.onDetach()
    }
}
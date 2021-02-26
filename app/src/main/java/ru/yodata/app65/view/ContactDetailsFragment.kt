package ru.yodata.app65.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactDetailsBinding
import ru.yodata.app65.utils.Constants.contactList

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {
    private var detailsFrag: FragmentContactDetailsBinding? = null

    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsFrag = FragmentContactDetailsBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                                            getString(R.string.contact_details_fragment_title)
        val curContact = contactList.last { it.id == contactId }
        with (curContact) {
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
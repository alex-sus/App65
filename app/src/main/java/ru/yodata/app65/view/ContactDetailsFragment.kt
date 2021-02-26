package ru.yodata.app65.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactDetailsBinding
import ru.yodata.app65.utils.Settings.CONTACT_ID
import ru.yodata.app65.utils.Settings.contactList

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {
    // Инициализация View Binding
    private var _detailsFrag: FragmentContactDetailsBinding? = null
    private val detailsFrag get() = _detailsFrag!!

    private var contactId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId = it.getInt(CONTACT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contact_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Необходимо для View Binding:
        _detailsFrag = FragmentContactDetailsBinding.bind(view)
        // Установить заголовок фрагмента
        (activity as AppCompatActivity).supportActionBar!!.title =
                                            getString(R.string.contact_details_fragment_title)
        // Заполнить поля фрагмента данными выбранного контакта
        val curContact = contactList.last { it.id == contactId }
        with (curContact) {
            with (detailsFrag) {
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
        // Необходимо для View Binding:
        _detailsFrag = null
        super.onDestroyView()
    }

    companion object {
        val FRAGMENT_NAME: String = ContactDetailsFragment::class.java.name
        @JvmStatic
        fun newInstance(contactId: Int) =
            ContactDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(CONTACT_ID, contactId)
                }
            }
    }
}
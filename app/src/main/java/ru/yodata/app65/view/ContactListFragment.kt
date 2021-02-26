package ru.yodata.app65.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ru.yodata.app65.R
import ru.yodata.app65.databinding.FragmentContactListBinding
import ru.yodata.app65.utils.Settings
import ru.yodata.app65.utils.Settings.CONTACT_ID

class ContactListFragment : Fragment(R.layout.fragment_contact_list) {
    // Инициализация View Binding
    private var _listFrag: FragmentContactListBinding? = null
    private val listFrag get() = _listFrag!!

    private var contactId: Int = 0
    private lateinit var mCallback: OnContactListCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnContactListCallback) {
            mCallback = context
        } else throw ClassCastException(context.toString() +
                                        "должен реализовать OnContactListCallback!")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            contactId = it.getInt(CONTACT_ID)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contact_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Необходимо для View Binding:
        _listFrag = FragmentContactListBinding.bind(view)
        // Установить заголовок фрагмента
        (activity as AppCompatActivity).supportActionBar!!.title =
                                                getString(R.string.contact_list_fragment_title)
        // Заполнить поля фрагмента данными текущего контакта
        val curContact = Settings.contactList.last { it.id == contactId }
        with (curContact) {
            with (listFrag) {
                nameTv.text = name
                phoneTv.text = phone1
            }
        }
        // Слушатель для перехода на следующий экран
        view.setOnClickListener { mCallback.navigateToContactDetailsFragment(contactId) }
    }

    override fun onDestroyView() {
        // Необходимо для View Binding:
        _listFrag = null
        super.onDestroyView()
    }

    companion object {
        val FRAGMENT_NAME: String = ContactListFragment::class.java.name
        @JvmStatic
        fun newInstance(contactId: Int) =
                ContactListFragment().apply {
                    arguments = Bundle().apply {
                        putInt(CONTACT_ID, contactId)
                    }
                }
    }
}
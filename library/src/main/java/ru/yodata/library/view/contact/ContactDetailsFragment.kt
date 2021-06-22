package ru.yodata.library.view.contact

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData
import ru.yodata.library.R
import ru.yodata.library.databinding.FragmentContactDetailsBinding
import ru.yodata.library.di.HasAppComponent
import ru.yodata.library.utils.Constants.EMPTY_VALUE
import ru.yodata.library.utils.Constants.TAG
import ru.yodata.library.utils.MapScreenMode
import ru.yodata.library.utils.injectViewModel
import ru.yodata.library.view.map.OnMapFragmentCallback
import ru.yodata.library.viewmodel.ContactDetailsViewModel
import java.util.*
import javax.inject.Inject

class ContactDetailsFragment : Fragment(R.layout.fragment_contact_details) {

    private var detailsFrag: FragmentContactDetailsBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactDetailsViewModel: ContactDetailsViewModel

    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }
    private var navigateCallback: OnMapFragmentCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMapFragmentCallback) {
            navigateCallback = context
        } else throw ClassCastException(
            context.toString() +
                    " must implement OnMapFragmentCallback!"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusContactDetailsContainer()
            ?.inject(this)
        contactDetailsViewModel = injectViewModel(viewModelFactory)
    }

    @InternalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detailsFrag = FragmentContactDetailsBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.contact_details_fragment_title)
        detailsFrag?.toMapFragmentFab?.setOnClickListener {
            navigateCallback?.navigateToBaseMapFragment(contactId, MapScreenMode.CONTACT)
        }
        detailsFrag?.deleteLocationDataFab?.setOnClickListener(deleteLocationDataFabListener)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            contactDetailsViewModel.getContactStateById(contactId).collect { state ->
                if (state != null) {
                    try {
                        val curContact = state.contactDetails
                        if (curContact != null) {
                            showContactDetails(curContact)
                            detailsFrag?.remindBtn?.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) contactDetailsViewModel.setBirthdayAlarm(curContact)
                                else contactDetailsViewModel.cancelBirthdayAlarm(curContact)
                            }
                        }
                        val curLocation = state.locationData
                        if (curLocation != null) {
                            showLocationData(curLocation)
                        }
                    } catch (e: IllegalStateException) {
                        Log.d(TAG, "Исключение в ContactDetailsFragment: ")
                        Log.d(TAG, e.stackTraceToString())
                    }

                }

            }
        }
        /*// Обзервер для данных текущего контакта, не включая данные о его местоположении
        contactDetailsViewModel.getContactById(contactId)
                .observe(viewLifecycleOwner, { curContact ->
                    if (curContact != null) {
                        try {
                            showContactDetails(curContact)
                            detailsFrag?.remindBtn?.setOnCheckedChangeListener { _, isChecked ->
                                if (isChecked) contactDetailsViewModel.setBirthdayAlarm(curContact)
                                else contactDetailsViewModel.cancelBirthdayAlarm(curContact)
                            }
                        } catch (e: IllegalStateException) {
                            Log.d(TAG, "Исключение в ContactDetailsFragment: ")
                            Log.d(TAG, e.stackTraceToString())
                        }
                    }
                })
        // Обзервер данных о местоположении текущего контакта
        contactDetailsViewModel.getLocationDataById(contactId)
                .observe(viewLifecycleOwner, { curLocation ->
                    try {
                        showLocationData(curLocation)
                    } catch (e: IllegalStateException) {
                        Log.d(TAG, "Исключение в ContactDetailsFragment: ")
                        Log.d(TAG, e.stackTraceToString())
                    }
                })*/
    }

    override fun onDestroyView() {
        detailsFrag = null
        super.onDestroyView()
    }

    override fun onDetach() {
        navigateCallback = null
        super.onDetach()
    }

    private fun showContactDetails(curContact: Contact) {
        with(curContact) {
            detailsFrag?.apply {
                fullNameTv.text = name
                birthdayTv.text = if (birthday != null)
                    birthday?.get(Calendar.DAY_OF_MONTH).toString() + " " +
                            birthday?.getDisplayName(
                                    Calendar.MONTH,
                                    Calendar.LONG,
                                    Locale.getDefault()
                            )
                else EMPTY_VALUE
                phone1Tv.text = phone1
                phone2Tv.text = phone2
                email1Tv.text = email1
                email2Tv.text = email2
                descriptionTv.text = description
                if (!bigPhotoUri.isNullOrEmpty()) {
                    bigPhotoIv.setImageURI(bigPhotoUri?.toUri())
                } else {
                    bigPhotoIv.setImageResource(R.drawable.programmer2_150)
                }

                remindBtn.isChecked = if (birthday != null) {
                    contactDetailsViewModel.isBirthdayAlarmOn(curContact)
                } else false
                remindBtn.visibility = if (birthday != null) View.VISIBLE else View.GONE
            }
        }
    }

    private fun showLocationData(curLocation: LocationData?) {
        if (curLocation != null) {
            with(curLocation) {
                detailsFrag?.apply {
                    latitudeTv.text = latitude.toString()
                    longitudeTv.text = longitude.toString()
                    addressTv.text = address
                    deleteLocationDataFab.visibility = View.VISIBLE
                }
            }
        } else {
            detailsFrag?.apply {
                latitudeTv.text = ""
                longitudeTv.text = ""
                addressTv.text = ""
            }
        }
    }

    private val deleteLocationDataFabListener = View.OnClickListener {
        detailsFrag?.deleteLocationDataFab?.visibility = View.GONE
        contactDetailsViewModel.deleteLocationDataById(contactId)
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



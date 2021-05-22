package ru.yodata.library.view.map

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.entities.LocationData
import ru.yodata.library.R
import ru.yodata.library.databinding.FragmentContactMapBinding
import ru.yodata.library.di.HasAppComponent
import ru.yodata.library.utils.Constants.GEOCODING_IN_PROGRESS_SYMBOL
import ru.yodata.library.utils.Constants.TAG
import ru.yodata.library.utils.injectViewModel
import ru.yodata.library.view.map.ContactMapSettings.MAP_TILT
import ru.yodata.library.view.map.ContactMapSettings.MAP_ZOOM
import ru.yodata.library.view.map.ContactMapSettings.START_LOCATION
import ru.yodata.library.viewmodel.ContactLocationsViewModel
import javax.inject.Inject

private const val CONTACT_ID = "id"

class ContactMapFragment : Fragment(R.layout.fragment_contact_map) {

    private var contactMapFrag: FragmentContactMapBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactLocationsViewModel: ContactLocationsViewModel

    /*private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }*/
    private lateinit var contactId: String
    private lateinit var map: GoogleMap
    private var mapReady = false
    private lateinit var curLocatedContact: LocatedContact
    private var curChangedLocationData: LocationData? = null
    private var isNewContactWithoutLocation = false
    private lateinit var curContactMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
                ?.getAppComponent()
                ?.plusContactMapContainer()
                ?.inject(this)
        contactLocationsViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contactMapFrag = FragmentContactMapBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.contact_location_screen_title)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        contactId = parentFragment?.arguments?.getString(CONTACT_ID, "") ?: ""
        // Обзервер работает с контактами, координаты которых уже есть в БД
        contactLocationsViewModel.getLocatedContactList()
                .observe(viewLifecycleOwner, { locatedContactList ->
                    // Попытаться найти текущий контакт в списке из БД координат
                    Log.d(TAG, "ContactMapFragment: начинаю поиск контакта в списке БД...")
                    Log.d(TAG, "locatedContactList = $locatedContactList")
                    val existLocatedContact = locatedContactList.firstOrNull { it.id == contactId }
                    if (existLocatedContact != null) {
                        curLocatedContact = existLocatedContact
                        mapFragment?.getMapAsync(onMapReadyCallback)
                        Log.d(TAG, "ContactMapFragment: старт карты в первом обзервере")
                        Log.d(TAG, "ContactMapFragment: поиск контакта в списке БД окончен.")
                        Log.d(TAG, "curLocatedContact = $curLocatedContact")
                        contactMapFrag?.apply {
                            curNameTv.text = curLocatedContact.name
                            if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                                curPhotoIv.setImageURI(curLocatedContact.photoUri?.toUri())
                            } else {
                                curPhotoIv.setImageResource(R.drawable.programmer2_70)
                            }
                            curLatitudeTv.text = curLocatedContact.latitude.toString()
                            curLongitudeTv.text = curLocatedContact.longitude.toString()
                            curAddressTv.text = curLocatedContact.address
                        }
                    } else {
                        // У текущего контакта еще не назначены координаты в БД
                        // поэтому нужно получить id, имя и фото контакта из ContentResolver
                        isNewContactWithoutLocation = true
                        contactLocationsViewModel.getBriefContactById(contactId)
                    }
                })
        // Обзервер работает с контактами, координат которых еще нет в БД
        contactLocationsViewModel.getBriefContactById(contactId)
                .observe(viewLifecycleOwner, { curBriefContact ->
                    if (isNewContactWithoutLocation) {
                        if (curBriefContact != null) {
                            curLocatedContact = LocatedContact(
                                    id = curBriefContact.id,
                                    name = curBriefContact.name,
                                    photoUri = curBriefContact.photoUri,
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    address = ""
                            )
                            mapFragment?.getMapAsync(onMapReadyCallback)
                            Log.d(TAG, "ContactMapFragment: старт карты во втором обзервере")
                            contactMapFrag?.apply {
                                curNameTv.text = curLocatedContact.name
                                if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                                    curPhotoIv.setImageURI(curLocatedContact.photoUri?.toUri())
                                } else {
                                    curPhotoIv.setImageResource(R.drawable.programmer2_70)
                                }
                            }
                        } else {
                            // id текущего контакта не найден в ContentResolver - такой ситуации вообще
                            // не должно быть при нормальной работе приложения
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                })
        // Обзервер обрабатывает изменения местоположения контакта на карте, при нажатиях
        // пользователя на ней
        contactLocationsViewModel.getChangedLocationData()
                .observe(viewLifecycleOwner, { curLocationData ->
                    if (curLocationData != null) {
                        contactMapFrag?.saveLocationFab?.isEnabled = false
                        curChangedLocationData = curLocationData
                        contactMapFrag?.apply {
                            curLatitudeTv.text = curLocationData.latitude.toString()
                            curLongitudeTv.text = curLocationData.longitude.toString()
                        }
                        if (curLocationData.address != GEOCODING_IN_PROGRESS_SYMBOL) {
                            contactMapFrag?.geocodingProgressBar?.visibility = View.GONE
                            contactMapFrag?.curAddressTv?.text = curLocationData.address
                            contactMapFrag?.saveLocationFab?.isEnabled = true
                        }
                    }
                })
        contactMapFrag?.saveLocationFab?.setOnClickListener { saveChangedLocationData() }
    }

    override fun onDestroyView() {
        contactMapFrag = null
        super.onDestroyView()
    }

    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true // добавить на карту кнопки масштабирования
        // Получить координаты измененного местоположения контакта (если менялось)
        var startLocation = contactLocationsViewModel.getChangedLatLngData()
        if (startLocation == null) { // пользователь не менял местоположение контакта
            Log.d(TAG, "startLocation == null")
            if (isNewContactWithoutLocation) { // у контакта еще нет местоположения в БД
                startLocation = START_LOCATION
            } else { // у контакта уже есть местоположение в БД
                startLocation = LatLng(curLocatedContact.latitude, curLocatedContact.longitude)
                if (::curContactMarker.isInitialized) curContactMarker.remove()
                curContactMarker = map.addMarker(MarkerOptions().position(startLocation))
            }
        } else { // пользователь изменил местоположение контакта и пока не сохранил его
            Log.d(TAG, "startLocation не null")
            if (::curContactMarker.isInitialized) curContactMarker.remove()
            curContactMarker = map.addMarker(MarkerOptions().position(startLocation))
            contactMapFrag?.saveLocationFab?.visibility = View.VISIBLE
        }
        val cameraPosition = CameraPosition.Builder()
                .target(startLocation)
                .zoom(MAP_ZOOM)
                .tilt(MAP_TILT)
                .build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        map.setOnMapClickListener(onMapClickListener)
        map.setOnMarkerClickListener { true } // отключает стандартную реакцию на щелчок по маркеру
        mapReady = true
    }

    private val onMapClickListener = GoogleMap.OnMapClickListener { point ->
        if (mapReady) {
            contactMapFrag?.geocodingProgressBar?.visibility = View.VISIBLE
            if (::curContactMarker.isInitialized) curContactMarker.remove()
            curContactMarker = map.addMarker(MarkerOptions().position(point))
            contactLocationsViewModel.setChangedLocationData(
                    LocationData(
                            latitude = point.latitude,
                            longitude = point.longitude,
                            // Временное значение адреса, потом заменится на найденое геокодингом
                            address = GEOCODING_IN_PROGRESS_SYMBOL
                    ))
            contactMapFrag?.saveLocationFab?.visibility = View.VISIBLE

        }
    }

    private fun saveChangedLocationData() {
        contactMapFrag?.saveLocationFab?.visibility = View.GONE
        val changedData = curChangedLocationData
        if (changedData != null) {
            curLocatedContact = curLocatedContact.copy(
                    latitude = changedData.latitude,
                    longitude = changedData.longitude,
                    address = changedData.address
            )
            if (isNewContactWithoutLocation) {
                contactLocationsViewModel.addLocatedContact(curLocatedContact)
            } else {
                contactLocationsViewModel.updateLocatedContact(curLocatedContact)
            }
        }
        curChangedLocationData = null
        contactLocationsViewModel.resetChangedLocationData()
        Toast.makeText(
                context,
                getString(R.string.location_saved_msg),
                Toast.LENGTH_LONG
        ).show()
        //requireActivity().supportFragmentManager.popBackStack()
    }

    private fun coordinatesToString(latitude: Double, longitude: Double) = "$latitude : $longitude"

    /*companion object {
        private const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = ContactMapFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String) =
                ContactMapFragment().apply {
                    arguments = Bundle().apply {
                        putString(CONTACT_ID, contactId)
                    }
                }
    }*/
}

// Настройки карты:
object ContactMapSettings {
    // Место, которое показывается на карте, если координаты еще не назначены (Ижевск)
    val START_LOCATION = LatLng(56.851, 53.214)

    // Масштаб изображения карты
    const val MAP_ZOOM = 13F

    // Угол наклона карты к наблюдателю
    const val MAP_TILT = 40F
}
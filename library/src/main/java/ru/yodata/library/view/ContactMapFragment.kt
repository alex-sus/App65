package ru.yodata.library.view

import android.os.Bundle
import android.util.Log
import android.view.View
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
import ru.yodata.library.utils.Constants.TAG
import ru.yodata.library.utils.injectViewModel
import ru.yodata.library.view.MapSettings.MAP_TILT
import ru.yodata.library.view.MapSettings.MAP_ZOOM
import ru.yodata.library.view.MapSettings.START_LOCATION
import ru.yodata.library.viewmodel.ContactLocationsViewModel
import javax.inject.Inject

class ContactMapFragment : Fragment(R.layout.fragment_contact_map) {

    private var mapFrag: FragmentContactMapBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var contactLocationsViewModel: ContactLocationsViewModel
    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }
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
        mapFrag = FragmentContactMapBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.contact_location_screen_title)
        contactLocationsViewModel.getLocatedContactList()
                .observe(viewLifecycleOwner, { locatedContactList ->
                    // Попытаться найти текущий контакт в списке из БД координат
                    Log.d(TAG, "ContactMapFragment: начинаю поиск контакта в списке БД...")
                    Log.d(TAG, "locatedContactList = $locatedContactList")
                    val existLocatedContact = locatedContactList.firstOrNull { it.id == contactId }
                    if (existLocatedContact != null) {
                        curLocatedContact = existLocatedContact
                    } else {
                        // У текущего контакта еще не назначены координаты в БД
                        // поэтому нужно взять id, имя и фото контакта из ContentResolver
                        isNewContactWithoutLocation = true
                        val curBriefContact =
                                contactLocationsViewModel.getBriefContactById(contactId)
                        if (curBriefContact != null) {
                            curLocatedContact = LocatedContact(
                                    id = curBriefContact.id,
                                    name = curBriefContact.name,
                                    photoUri = curBriefContact.photoUri,
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    address = ""
                            )
                        } else {
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                    }
                    Log.d(TAG, "ContactMapFragment: поиск контакта в списке БД окончен.")
                    Log.d(TAG, "curLocatedContact = $curLocatedContact")
                    mapFrag?.apply {
                        curNameTv.text = curLocatedContact.name
                        if (!curLocatedContact.photoUri.isNullOrEmpty()) {
                            curPhotoIv.setImageURI(curLocatedContact.photoUri?.toUri())
                        } else {
                            curPhotoIv.setImageResource(R.drawable.programmer2_70)
                        }
                        if (!isNewContactWithoutLocation) {
                            curCoordinatesTv.text = coordinatesToString(
                                    curLocatedContact.latitude,
                                    curLocatedContact.longitude)
                            curAddressTv.text = curLocatedContact.address
                        }
                    }
                })
        contactLocationsViewModel.getChangedLocationData()
                .observe(viewLifecycleOwner, { curLocationData ->
                    if (curLocationData != null) {
                        curChangedLocationData = curLocationData
                        mapFrag?.apply {
                            curCoordinatesTv.text = coordinatesToString(
                                    curLocationData.latitude,
                                    curLocationData.longitude
                            )
                            curAddressTv.text = curLocationData.address
                        }
                    }
                })
        mapFrag?.saveLocationFab?.setOnClickListener { saveChangedLocationData() }
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(onMapReadyCallback)
    }

    override fun onDestroyView() {
        mapFrag = null
        super.onDestroyView()
    }

    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true // добавить на карту кнопки масштабирования
        // Настроить параметры камеры и отобразить карту с маркером контакта в центре (если есть)
        var startLocation = contactLocationsViewModel.getChangedLatLngData()
        if (startLocation == null) {
            if (isNewContactWithoutLocation) {
                startLocation = START_LOCATION
            } else {
                startLocation = LatLng(curLocatedContact.latitude, curLocatedContact.longitude)
                curContactMarker = map.addMarker(MarkerOptions().position(startLocation))
            }
        } else {
            curContactMarker = map.addMarker(MarkerOptions().position(startLocation))
            mapFrag?.saveLocationFab?.visibility = View.VISIBLE
        }
        val cameraPosition = CameraPosition.Builder()
                .target(startLocation)
                .zoom(MAP_ZOOM)
                .tilt(MAP_TILT)
                .build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        map.setOnMapClickListener(onMapClickListener)
        mapReady = true
    }

    private val onMapClickListener = GoogleMap.OnMapClickListener { point ->
        if (mapReady) {
            if (::curContactMarker.isInitialized) curContactMarker.remove()
            curContactMarker = map.addMarker(MarkerOptions().position(point))
            contactLocationsViewModel.setChangedLocationData(
                    LocationData(
                            latitude = point.latitude,
                            longitude = point.longitude,
                            address = backGeocoding(point)
                    ))
            mapFrag?.saveLocationFab?.visibility = View.VISIBLE

        }
    }

    private fun saveChangedLocationData() {
        mapFrag?.saveLocationFab?.visibility = View.GONE
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
        /*Toast.makeText(
                context,
                getString(R.string.location_saved_msg),
                Toast.LENGTH_LONG
        ).show()*/
        requireActivity().supportFragmentManager.popBackStack()
    }

    private fun backGeocoding(point: LatLng): String {
        // TODO Здесь нужно сделать обратный геокодинг
        return "г.Ижевск ул.Удмуртская 156"
    }

    private fun coordinatesToString(latitude: Double, longitude: Double) = "$latitude : $longitude"

    companion object {
        private const val CONTACT_ID = "id"
        val FRAGMENT_NAME: String = ContactMapFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String) =
                ContactMapFragment().apply {
                    arguments = Bundle().apply {
                        putString(CONTACT_ID, contactId)
                    }
                }
    }
}

// Настройки карты:
object MapSettings {
    // Место, которое показывается на карте, если координаты еще не назначены (Ижевск)
    val START_LOCATION = LatLng(56.851, 53.214)

    // Масштаб изображения карты
    const val MAP_ZOOM = 13F

    // Угол наклона карты к наблюдателю
    const val MAP_TILT = 40F

    // Для режима показа нескольких маркеров одновременно - внутренние поля прямоугольной области,
    // содержащей эти маркеры (в dp)
    const val MAP_MARKERS_PADDING = 90
}
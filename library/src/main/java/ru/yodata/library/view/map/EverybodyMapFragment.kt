package ru.yodata.library.view.map

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import ru.yodata.java.entities.LocatedContact
import ru.yodata.library.R
import ru.yodata.library.databinding.FragmentContactMapBinding
import ru.yodata.library.di.HasAppComponent
import ru.yodata.library.utils.MapScreenMode
import ru.yodata.library.utils.injectViewModel
import ru.yodata.library.view.map.EverybodyMapSettings.COMMON_MARKERS_COLOR
import ru.yodata.library.view.map.EverybodyMapSettings.MAP_MARKERS_PADDING
import ru.yodata.library.viewmodel.EverybodyMapViewModel
import javax.inject.Inject

private const val CONTACT_ID = "id"
private const val SCREEN_MODE = "mode"

class EverybodyMapFragment : Fragment(R.layout.fragment_contact_map) {

    private var everybodyMapFrag: FragmentContactMapBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var everybodyMapViewModel: EverybodyMapViewModel

    //lateinit var mapMenuView: BottomNavigationView
    /*private val curContactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }*/
    private lateinit var curContactId: String
    private lateinit var locatedContactList: List<LocatedContact>
    private lateinit var curLocatedContact: LocatedContact
    private lateinit var map: GoogleMap

    //private var mapReady = false
    private lateinit var curContactMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
                ?.getAppComponent()
                ?.plusEverybodyMapContainer()
                ?.inject(this)
        everybodyMapViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        everybodyMapFrag = FragmentContactMapBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.everybody_location_screen_title)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        curContactId = parentFragment?.arguments?.getString(CONTACT_ID, "") ?: ""
        everybodyMapViewModel.getLocatedContactList()
                .observe(viewLifecycleOwner, {
                    locatedContactList = it
                    if (locatedContactList.isNotEmpty()) {
                        // Если у текущего контакта нет координат в БД, то и показать его на карте
                        // не получится. В этом случае текущим контактом принудительно становится
                        // первый же контакт из БД, у которого координаты заведомо имеются
                        curLocatedContact = locatedContactList
                                .firstOrNull { it.id == curContactId } ?: locatedContactList[0]
                        // Так как текущим контактом мог стать произвольный контакт, следует
                        // обновить данные об id текущего контакта
                        curContactId = curLocatedContact.id
                        parentFragment?.arguments = Bundle().apply {
                            putString(CONTACT_ID, curContactId)
                            putSerializable(SCREEN_MODE, MapScreenMode.EVERYBODY)
                        }
                        mapFragment?.getMapAsync(onMapReadyCallback)
                        showLocatedContactDetails(curLocatedContact)
                    }
                })
    }

    override fun onDestroyView() {
        everybodyMapFrag = null
        super.onDestroyView()
    }

    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true // добавить на карту кнопки масштабирования
        val curLocation = LatLng(curLocatedContact.latitude, curLocatedContact.longitude)
        if (::curContactMarker.isInitialized) curContactMarker.remove()
        curContactMarker = map.addMarker(MarkerOptions()
                .position(curLocation)
                .snippet(curLocatedContact.id)
        )
        if (locatedContactList.size == 1) {
            val cameraPosition = CameraPosition.Builder()
                    .target(curLocation)
                    .zoom(ContactMapSettings.MAP_ZOOM)
                    .tilt(ContactMapSettings.MAP_TILT)
                    .build()
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        } else {
            locatedContactList.forEach {
                if (it.id != curLocatedContact.id) {
                    map.addMarker(MarkerOptions()
                            .position(LatLng(it.latitude, it.longitude))
                            .snippet(it.id)
                            .icon(BitmapDescriptorFactory.defaultMarker(COMMON_MARKERS_COLOR))
                    )
                }
            }
            map.moveCamera(CameraUpdateFactory
                    .newLatLngBounds(
                            locatedContactListLatLngBounds(locatedContactList),
                            MAP_MARKERS_PADDING
                    )
            )
        }
        map.setOnMarkerClickListener(onMarkerClickListener)
    }

    private val onMarkerClickListener = GoogleMap.OnMarkerClickListener { newMarker ->
        if (newMarker != curContactMarker) {
            curContactMarker.setIcon(BitmapDescriptorFactory.defaultMarker(COMMON_MARKERS_COLOR))
            curContactMarker = newMarker
            curContactMarker.setIcon(BitmapDescriptorFactory.defaultMarker())
            curLocatedContact = locatedContactList.first { it.id == curContactMarker.snippet }
            showLocatedContactDetails(curLocatedContact)
            curContactId = curLocatedContact.id
            parentFragment?.arguments = Bundle().apply {
                putString(CONTACT_ID, curContactId)
                putSerializable(SCREEN_MODE, MapScreenMode.EVERYBODY)
            }
        }
        true // если тут будет false, то карта будет отцентрована по этому маркеру, что нам не нужно
    }

    fun showLocatedContactDetails(locatedContact: LocatedContact) {
        with(locatedContact) {
            everybodyMapFrag?.apply {
                curNameTv.text = name
                if (!photoUri.isNullOrEmpty()) {
                    curPhotoIv.setImageURI(photoUri?.toUri())
                } else {
                    curPhotoIv.setImageResource(R.drawable.programmer2_70)
                }
                curLatitudeTv.text = latitude.toString()
                curLongitudeTv.text = longitude.toString()
                curAddressTv.text = address
            }
        }
    }

    fun locatedContactListLatLngBounds(locatedList: List<LocatedContact>): LatLngBounds {
        var southwest = LatLng(locatedList[0].latitude, locatedList[0].longitude)
        var northeast = southwest
        if (locatedList.size > 1) {
            locatedList.forEach {
                southwest = LatLng(it.latitude.coerceAtMost(southwest.latitude),
                        it.longitude.coerceAtMost(southwest.longitude))
                northeast = LatLng(it.latitude.coerceAtLeast(northeast.latitude),
                        it.longitude.coerceAtLeast(northeast.longitude))
            }
        }
        return LatLngBounds(southwest, northeast)
    }

    /*companion object {
        private const val CONTACT_ID = "id"
        private const val SCREEN_MODE = "mode"
        val FRAGMENT_NAME: String = EverybodyMapFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String) =
                EverybodyMapFragment().apply {
                    arguments = Bundle().apply {
                        putString(CONTACT_ID, contactId)
                    }
                }
    }*/
}

// Настройки карты:
object EverybodyMapSettings {

    // Масштаб изображения карты
    const val MAP_ZOOM = 13F

    // Угол наклона карты к наблюдателю
    const val MAP_TILT = 40F

    // Для режима показа нескольких маркеров одновременно:
    // Внутренние поля прямоугольной области, содержащей эти маркеры (в dp)
    const val MAP_MARKERS_PADDING = 100

    // Цвет обычных маркеров (чтобы отличались от текущего маркера)
    const val COMMON_MARKERS_COLOR = BitmapDescriptorFactory.HUE_VIOLET
}
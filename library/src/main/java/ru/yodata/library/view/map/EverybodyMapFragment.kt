package ru.yodata.library.view.map

import android.annotation.SuppressLint
import android.os.Bundle
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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
    private lateinit var everybodyMapViewModel: EverybodyMapViewModel
    private lateinit var curContactId: String
    private lateinit var locatedContactList: List<LocatedContact>
    private lateinit var curLocatedContact: LocatedContact
    private lateinit var map: GoogleMap
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
            .observe(viewLifecycleOwner, { list ->
                locatedContactList = list
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
                    } else { // пока нет ни одного контакта с координатами в БД - нечего отображать
                        everybodyMapFrag?.curContactGroup?.visibility = View.INVISIBLE
                        Toast.makeText(
                                context,
                                getString(R.string.not_enough_contact_locations_msg),
                                Toast.LENGTH_LONG
                        ).show()

                    }
                })
    }

    override fun onDestroyView() {
        everybodyMapFrag = null
        super.onDestroyView()
    }

    @SuppressLint("PotentialBehaviorOverride")
    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true // добавить на карту кнопки масштабирования
        val curLocation = LatLng(curLocatedContact.latitude, curLocatedContact.longitude)
        if (::curContactMarker.isInitialized) curContactMarker.remove()
        map.addMarker(
            MarkerOptions()
                .position(curLocation)
                .snippet(curLocatedContact.id)
        )?.let { curContactMarker = it }
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

    private fun showLocatedContactDetails(locatedContact: LocatedContact) {
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

    private fun locatedContactListLatLngBounds(locatedList: List<LocatedContact>) =
            LatLngBounds.Builder().let { builder ->
                locatedList.forEach {
                    builder.include(LatLng(it.latitude, it.longitude))
                }
                builder.build()
            }

}

// Настройки карты:
object EverybodyMapSettings {

    // Для режима показа нескольких маркеров одновременно:
    // Внутренние поля прямоугольной области, содержащей эти маркеры (в dp)
    const val MAP_MARKERS_PADDING = 100

    // Цвет обычных маркеров (чтобы отличались от текущего маркера)
    const val COMMON_MARKERS_COLOR = BitmapDescriptorFactory.HUE_VIOLET
}
package ru.yodata.library.view.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import ru.yodata.java.entities.LocatedContact
import ru.yodata.library.R
import ru.yodata.library.databinding.FragmentContactMapBinding
import ru.yodata.library.di.HasAppComponent
import ru.yodata.library.utils.Constants.TAG
import ru.yodata.library.utils.MapScreenMode
import ru.yodata.library.utils.injectViewModel
import ru.yodata.library.view.map.RouteMapSettings.MAP_MARKERS_PADDING
import ru.yodata.library.view.map.RouteMapSettings.ROUTE_MODE
import ru.yodata.library.view.map.RouteMapSettings.ROUTE_POLYLINE_COLOR
import ru.yodata.library.view.map.RouteMapSettings.ROUTE_POLYLINE_WIDTH
import ru.yodata.library.viewmodel.RouteMapViewModel
import javax.inject.Inject

private const val CONTACT_ID = "id"
const val SELECTED_DIALOG_ELEMENT_NUMBER = "id2"
private const val SCREEN_MODE = "mode"

class RouteMapFragment : Fragment(R.layout.fragment_contact_map) {

    private var routeMapFrag: FragmentContactMapBinding? = null

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var routeMapViewModel: RouteMapViewModel
    private lateinit var curContactId: String
    private lateinit var locatedContactList: List<LocatedContact>
    private lateinit var curLocatedContact: LocatedContact
    private lateinit var secondLocatedContact: LocatedContact
    private lateinit var map: GoogleMap
    private lateinit var curContactMarker: Marker
    private lateinit var secondContactMarker: Marker
    private lateinit var routePolyline: Polyline
    private lateinit var secondContactDialogFragment: SecondContactDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (requireActivity().application as? HasAppComponent)
            ?.getAppComponent()
            ?.plusRouteMapContainer()
            ?.inject(this)
        routeMapViewModel = injectViewModel(viewModelFactory)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        routeMapFrag = FragmentContactMapBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.title =
                getString(R.string.route_screen_title)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        curContactId = parentFragment?.arguments?.getString(CONTACT_ID, "") ?: ""
        // Обзервер списка контактов с координатами
        routeMapViewModel.getLocatedContactList()
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
                        putSerializable(SCREEN_MODE, MapScreenMode.ROUTE)
                    }
                    mapFragment?.getMapAsync(onMapReadyCallback)
                    showFirstContactDetails(curLocatedContact)
                    secondContactDialogFragment = SecondContactDialogFragment
                        .newInstance(locatedContactList.map { it.name } as ArrayList<String>)
                    setFragmentResultListener(DIALOG_REQUEST_KEY, setSecondContactListener)
                } else { // пока нет ни одного контакта с координатами в БД - нечего отображать
                    routeMapFrag?.curContactGroup?.visibility = View.INVISIBLE
                        Toast.makeText(
                                context,
                                getString(R.string.not_enough_contact_locations_msg),
                                Toast.LENGTH_LONG
                        ).show()
                    }
                })
    }

    override fun onDestroyView() {
        routeMapFrag = null
        super.onDestroyView()
    }

    private val onMapReadyCallback = OnMapReadyCallback { googleMap ->
        map = googleMap
        map.uiSettings.isZoomControlsEnabled = true // добавить на карту кнопки масштабирования
        val curLocation = LatLng(curLocatedContact.latitude, curLocatedContact.longitude)
        if (::curContactMarker.isInitialized) curContactMarker.remove()
        // Отобразить первый маркер (текущего контакта)
        map.addMarker(
            MarkerOptions()
                .position(curLocation)
                .title(curLocatedContact.name)
        )?.let { curContactMarker = it }
        val cameraPosition = CameraPosition.Builder()
            .target(curLocation)
            .zoom(ContactMapSettings.MAP_ZOOM)
            .tilt(ContactMapSettings.MAP_TILT)
            .build()
        map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        routeMapFrag?.addRouteDestinationFab?.setOnClickListener(addRouteDestinationFabListener)
        routeMapFrag?.addRouteDestinationFab?.visibility = View.VISIBLE
    }

    private val addRouteDestinationFabListener = View.OnClickListener {
        if (locatedContactList.size <= 1) {
            // Если нет хотя бы 2 контактов с координатами, построить маршрут не получится.
            // Пока пользователь не добавит второй контакт на другом экране, здесь ничего
            // происходить не будет
            Toast.makeText(
                    context,
                    getString(R.string.not_enough_contact_locations_msg),
                    Toast.LENGTH_LONG
            ).show()
        } else {
            // Запросить у пользователя второй контакт в диалоговом окне со списком контактов
            secondContactDialogFragment.show(parentFragmentManager, TAG)
        }
    }

    private val setSecondContactListener: (String, Bundle) -> Unit = { _, bundle ->
        // Второй контакт выбирается пользователем из списка в диалоге, номер выбранного элемента
        // списка приходит в bundle
        val number = bundle.getInt(SELECTED_DIALOG_ELEMENT_NUMBER)
        Log.d(TAG, "RouteMapFragment: number = $number")
        val secondContactId = locatedContactList[number].id
        secondLocatedContact = locatedContactList[number]
        showSecondContactDetails(secondLocatedContact)
        // Запустить асинхронно поиск и отрисовку на карте маршрута между двумя контактами
        // (отрабатывается в обзервере ниже)
        routeMapViewModel.requestContactRouteDecodedDetails(
                curContactId,
                secondContactId,
                ROUTE_MODE
        )
        // Обзервер для рисования текущего маршрута
        routeMapViewModel.getContactRouteDecodedDetails()
            .observe(viewLifecycleOwner, {
                if (::routePolyline.isInitialized) routePolyline.remove()
                if (it != null) {
                    routeMapFrag?.routeTv?.text = it.distanceString
                    routePolyline = map.addPolyline(
                        it.routePolylineOptions
                            .width(ROUTE_POLYLINE_WIDTH)
                            .color(ROUTE_POLYLINE_COLOR)
                            .geodesic(true)
                    )
                    map.animateCamera(
                        CameraUpdateFactory
                            .newLatLngBounds(
                                it.routeBounds,
                                MAP_MARKERS_PADDING
                            )
                    )
                } else routeMapFrag?.routeTv?.text = getString(R.string.no_route_msg)
            })
        if (::secondContactMarker.isInitialized) secondContactMarker.remove()
        // Отобразить маркер второго контакта на карте, смасштабировав ее, чтобы поместились оба
        map.addMarker(
            MarkerOptions()
                .position(LatLng(secondLocatedContact.latitude, secondLocatedContact.longitude))
                .title(secondLocatedContact.name)
        )?.let { secondContactMarker = it }
        map.moveCamera(
            CameraUpdateFactory
                .newLatLngBounds(
                    LatLngBounds.Builder()
                        .include(
                            LatLng(
                                curLocatedContact.latitude,
                                curLocatedContact.longitude
                            )
                        )
                        .include(
                            LatLng(
                                secondLocatedContact.latitude,
                                secondLocatedContact.longitude
                            )
                        )
                        .build(),
                    MAP_MARKERS_PADDING
                )
        )
    }

    private fun showFirstContactDetails(locatedContact: LocatedContact) {
        with(locatedContact) {
            routeMapFrag?.apply {
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

    private fun showSecondContactDetails(locatedContact: LocatedContact) {
        with(locatedContact) {
            routeMapFrag?.apply {
                curNameTv2.text = name
                if (!photoUri.isNullOrEmpty()) {
                    curPhotoIv2.setImageURI(photoUri?.toUri())
                } else {
                    curPhotoIv2.setImageResource(R.drawable.programmer2_70)
                }
                curLatitudeTv2.text = latitude.toString()
                curLongitudeTv2.text = longitude.toString()
                curAddressTv2.text = address
            }
        }
        routeMapFrag?.secondContactGroup?.visibility = View.VISIBLE
    }

}


// Настройки карты:
object RouteMapSettings {

    // Для режима показа нескольких маркеров одновременно:
    // Внутренние поля прямоугольной области, содержащей эти маркеры (в dp)
    const val MAP_MARKERS_PADDING = 100

    // Режим построения маршрута
    const val ROUTE_MODE = "driving" // walking, bicycling, transit

    // Параметры полилинии маршрута:
    const val ROUTE_POLYLINE_WIDTH = 10F // толщина
    const val ROUTE_POLYLINE_COLOR = Color.RED // цвет

}
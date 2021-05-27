package ru.yodata.library.view.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.yodata.library.R
import ru.yodata.library.utils.MapScreenMode

// Этот фрагмент является родительским для трех фрагментов карты: ContactMapFragment,
// EverybodyMapFragment, RouteMapFragment
// Содержит нижнее меню и контейнер для запуска в нем дочерних фрагментов
class BaseMapFragment : Fragment() {

    lateinit var mapMenuView: BottomNavigationView
    private val contactId: String by lazy {
        requireArguments().getString(CONTACT_ID, "")
    }
    private val screenMode: MapScreenMode by lazy {
        requireArguments().getSerializable(SCREEN_MODE) as MapScreenMode
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_base_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapMenuView = view.findViewById(R.id.mapMenuNavView)
        mapMenuView.setOnNavigationItemSelectedListener(mapMenuItemSelectedListener)
        if (savedInstanceState == null) {
            // В зависимости от заданного стартового экрана активировать соответствующий пункт
            // нижнего меню
            when (screenMode) {
                MapScreenMode.CONTACT -> {
                    mapMenuView.selectedItemId = R.id.singleItem
                }
                MapScreenMode.ROUTE -> {
                    mapMenuView.selectedItemId = R.id.routeItem
                }
                MapScreenMode.EVERYBODY -> {
                    mapMenuView.selectedItemId = R.id.everybodyItem
                }
            }
        }
    }

    private val mapMenuItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.singleItem -> {
                        this.arguments = Bundle().apply {
                            putString(CONTACT_ID, requireArguments().getString(CONTACT_ID, ""))
                            putSerializable(SCREEN_MODE, MapScreenMode.CONTACT)
                        }
                        openSelectedFragment(ContactMapFragment()) //.newInstance(contactId))
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.routeItem -> {
                        this.arguments = Bundle().apply {
                            putString(CONTACT_ID, requireArguments().getString(CONTACT_ID, ""))
                            putSerializable(SCREEN_MODE, MapScreenMode.ROUTE)
                        }
                        openSelectedFragment(RouteMapFragment())
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.everybodyItem -> {
                        this.arguments = Bundle().apply {
                            putString(CONTACT_ID, requireArguments().getString(CONTACT_ID, ""))
                            putSerializable(SCREEN_MODE, MapScreenMode.EVERYBODY)
                        }
                        openSelectedFragment(EverybodyMapFragment()) //.newInstance(contactId))
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }

    private fun openSelectedFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
                .replace(
                        R.id.mapFragmentsContainer,
                        fragment,
                )
                //.addToBackStack(null)
                .commit()
    }

    /*private fun getCurrentContactId(): String =
            requireArguments().getString(CONTACT_ID, "")
*/
    companion object {
        private const val CONTACT_ID = "id"
        private const val SCREEN_MODE = "mode"
        val FRAGMENT_NAME: String = BaseMapFragment::class.java.name

        @JvmStatic
        fun newInstance(contactId: String, screenMode: MapScreenMode) =
                BaseMapFragment().apply {
                    arguments = Bundle().apply {
                        putString(CONTACT_ID, contactId)
                        putSerializable(SCREEN_MODE, screenMode)
                    }
                }
    }
}
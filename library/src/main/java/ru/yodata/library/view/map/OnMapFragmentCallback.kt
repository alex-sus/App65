package ru.yodata.library.view.map

import ru.yodata.library.utils.MapScreenMode

interface OnMapFragmentCallback {

    fun navigateToBaseMapFragment(contactId: String, screenMode: MapScreenMode)
}
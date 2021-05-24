package ru.yodata.library.di

import ru.yodata.library.view.map.RouteMapFragment

interface RouteMapContainer {

    fun inject(routeMapFragment: RouteMapFragment)
}
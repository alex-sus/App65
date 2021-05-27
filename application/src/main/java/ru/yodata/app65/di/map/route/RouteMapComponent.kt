package ru.yodata.app65.di.map.route

import dagger.Subcomponent
import ru.yodata.library.di.RouteMapContainer

@RouteMapScope
@Subcomponent(modules = [RouteMapViewModelModule::class, RouteMapModule::class])

interface RouteMapComponent : RouteMapContainer
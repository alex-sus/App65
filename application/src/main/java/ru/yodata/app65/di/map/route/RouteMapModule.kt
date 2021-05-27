package ru.yodata.app65.di.map.route

import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.GoogleDirectionsRepositoryInterface
import ru.yodata.java.interactors.map.route.RouteMapInteractor
import ru.yodata.java.interactors.map.route.RouteMapModel

@Module
class RouteMapModule {

    @RouteMapScope
    @Provides
    fun provideRouteMapInteractor(
        locationRepository: ContactLocationRepositoryInterface,
        routeRepository: GoogleDirectionsRepositoryInterface
    ): RouteMapInteractor = RouteMapModel(
        locationRepository,
        routeRepository
    )
}
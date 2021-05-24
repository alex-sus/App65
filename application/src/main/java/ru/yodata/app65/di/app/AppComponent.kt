package ru.yodata.app65.di.app

import dagger.Component
import ru.yodata.app65.di.contact.ContactDetailsComponent
import ru.yodata.app65.di.contacts.ContactListComponent
import ru.yodata.app65.di.map.contact.ContactMapComponent
import ru.yodata.app65.di.map.everybody.EverybodyMapComponent
import ru.yodata.app65.di.map.route.RouteMapComponent
import ru.yodata.app65.di.viewmodel_factory.ViewModelFactoryModule
import ru.yodata.library.di.AppContainer
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AppModule::class,
    ViewModelFactoryModule::class,
    RepositoryModule::class,
    ContactLocationDatabaseModule::class,
    RetrofitModule::class
])
interface AppComponent : AppContainer {
    override fun plusContactListContainer(): ContactListComponent
    override fun plusContactDetailsContainer(): ContactDetailsComponent
    override fun plusContactMapContainer(): ContactMapComponent
    override fun plusEverybodyMapContainer(): EverybodyMapComponent
    override fun plusRouteMapContainer(): RouteMapComponent
}
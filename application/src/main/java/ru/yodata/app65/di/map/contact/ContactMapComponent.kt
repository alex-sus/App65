package ru.yodata.app65.di.map.contact

import dagger.Subcomponent
import ru.yodata.library.di.ContactMapContainer

@ContactMapScope
@Subcomponent(modules = [ContactLocationsViewModelModule::class, ContactMapModule::class])
interface ContactMapComponent : ContactMapContainer
package ru.yodata.app65.di.map.everybody

import dagger.Subcomponent
import ru.yodata.library.di.EverybodyMapContainer

@EverybodyMapScope
@Subcomponent(modules = [EverybodyMapViewModelModule::class, EverybodyMapModule::class])
interface EverybodyMapComponent : EverybodyMapContainer

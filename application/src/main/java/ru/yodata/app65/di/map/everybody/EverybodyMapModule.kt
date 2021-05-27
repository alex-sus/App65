package ru.yodata.app65.di.map.everybody

import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.map.everybody.EverybodyMapInteractor
import ru.yodata.java.interactors.map.everybody.EverybodyMapModel

@Module
class EverybodyMapModule {

    @EverybodyMapScope
    @Provides
    fun provideEverybodyMapInteractor(
            locationRepository: ContactLocationRepositoryInterface
    ): EverybodyMapInteractor = EverybodyMapModel(locationRepository)

}
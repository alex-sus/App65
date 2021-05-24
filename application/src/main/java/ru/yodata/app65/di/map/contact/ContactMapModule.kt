package ru.yodata.app65.di.map.contact

import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.java.interactors.YandexGeocoderRepositoryInterface
import ru.yodata.java.interactors.map.contact.ContactMapInteractor
import ru.yodata.java.interactors.map.contact.ContactMapModel

@Module
class ContactMapModule {

    @ContactMapScope
    @Provides
    fun provideContactMapInteractor(
            contactRepository: ContactRepositoryInterface,
            locationRepository: ContactLocationRepositoryInterface,
            geocoderRepository: YandexGeocoderRepositoryInterface
    ): ContactMapInteractor =
            ContactMapModel(
                    contactRepository,
                    locationRepository,
                    geocoderRepository
            )
}
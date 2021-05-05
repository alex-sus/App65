package ru.yodata.app65.di.map

import dagger.Module
import dagger.Provides
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.java.interactors.map.ContactMapInteractor
import ru.yodata.java.interactors.map.ContactMapModel

@Module
class ContactMapModule {

    @ContactMapScope
    @Provides
    fun provideContactMapInteractor(
            contactRepository: ContactRepositoryInterface,
            locRepository: ContactLocationRepositoryInterface)
            : ContactMapInteractor = ContactMapModel(contactRepository, locRepository)
}
package ru.yodata.app65.di.contact

import dagger.Subcomponent
import ru.yodata.library.di.ContactDetailsContainer

@ContactsDetailsScope
@Subcomponent(modules = [ContactDetailsViewModelModule::class, ContactDetailsModule::class])
interface ContactDetailsComponent : ContactDetailsContainer
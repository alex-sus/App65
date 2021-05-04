package ru.yodata.app65.di.contacts

import dagger.Subcomponent
import ru.yodata.library.di.ContactListContainer

@ContactsListScope
@Subcomponent(modules = [ContactListViewModelModule::class, ContactListModule::class])
interface ContactListComponent : ContactListContainer
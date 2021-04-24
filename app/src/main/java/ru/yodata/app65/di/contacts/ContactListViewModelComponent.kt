package ru.yodata.app65.di.contacts

import dagger.Subcomponent
import ru.yodata.app65.view.ContactListFragment

@ContactsListScope
@Subcomponent(modules = [ContactListViewModelModule::class])
interface ContactListViewModelComponent {
    fun inject(contactListFragment: ContactListFragment)
}
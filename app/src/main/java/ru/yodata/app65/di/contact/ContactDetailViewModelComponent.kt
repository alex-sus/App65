package ru.yodata.app65.di.contact

import dagger.Subcomponent
import ru.yodata.app65.view.ContactDetailsFragment

@ContactsDetailsScope
@Subcomponent(modules = [ContactDetailViewModelModule::class])
interface ContactDetailViewModelComponent {
    fun inject(contactDetailsFragment: ContactDetailsFragment)
}
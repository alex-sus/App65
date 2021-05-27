package ru.yodata.library.di

import ru.yodata.library.view.contact.ContactDetailsFragment

interface ContactDetailsContainer {
    fun inject(contactDetailsFragment: ContactDetailsFragment)
}
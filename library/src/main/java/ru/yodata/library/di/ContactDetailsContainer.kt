package ru.yodata.library.di

import ru.yodata.library.view.ContactDetailsFragment

interface ContactDetailsContainer {
    fun inject(contactDetailsFragment: ContactDetailsFragment)
}
package ru.yodata.library.di

import ru.yodata.library.view.ContactMapFragment

interface ContactMapContainer {
    fun inject(contactMapFragment: ContactMapFragment)
}
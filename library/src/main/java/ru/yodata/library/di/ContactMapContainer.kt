package ru.yodata.library.di

import ru.yodata.library.view.map.ContactMapFragment

interface ContactMapContainer {
    fun inject(contactMapFragment: ContactMapFragment)
}
package ru.yodata.library.di

import ru.yodata.library.view.ContactListFragment

interface ContactListContainer {
    fun inject(contactListFragment: ContactListFragment)
}
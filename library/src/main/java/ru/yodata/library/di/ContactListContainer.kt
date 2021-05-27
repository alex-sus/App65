package ru.yodata.library.di

import ru.yodata.library.view.contacts.ContactListFragment

interface ContactListContainer {
    fun inject(contactListFragment: ContactListFragment)
}
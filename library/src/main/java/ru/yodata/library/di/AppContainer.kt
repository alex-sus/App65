package ru.yodata.library.di

interface AppContainer {
    fun plusContactListContainer(): ContactListContainer
    fun plusContactDetailsContainer(): ContactDetailsContainer
    fun plusContactMapContainer(): ContactMapContainer
}
package ru.yodata.java.interactors.map.route

import ru.yodata.java.entities.LocatedContact

interface RouteMapInteractor {

    suspend fun getLocatedContactList(): List<LocatedContact>?
}
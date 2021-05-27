package ru.yodata.java.interactors.map.everybody

import ru.yodata.java.entities.LocatedContact

interface EverybodyMapInteractor {

    suspend fun getLocatedContactList(): List<LocatedContact>?
}
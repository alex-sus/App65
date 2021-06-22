package ru.yodata.library.data.repository

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import ru.yodata.java.entities.LocatedContact
import ru.yodata.java.entities.LocationData
import ru.yodata.java.interactors.ContactLocationRepositoryInterface
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.library.data.room.database.ContactLocationDatabase
import ru.yodata.library.data.room.entity.toDatabase
import ru.yodata.library.utils.Constants.TAG

class ContactLocationRepository(
        private val db: ContactLocationDatabase,
        private val contactRepositoryInterface: ContactRepositoryInterface
) : ContactLocationRepositoryInterface {

    override suspend fun getLocationDataById(contactId: String): Flow<LocationData?> =
            flow {
                emit(
                db.contactLocationDao().getContactLocationById(contactId)?.let {
                    LocationData(
                            latitude = it.latitude,
                            longitude = it.longitude,
                            address = it.address
                    )
                } )
            }.flowOn(Dispatchers.IO)

    override suspend fun getLocatedContactList(): List<LocatedContact>? =
            withContext(Dispatchers.IO) {
                db.contactLocationDao().getAllContactLocations()?.mapNotNull {
                    // Параллельный запуск создания элементов списка locatedContactList
                    withContext(Dispatchers.IO) {
                        val briefContact =
                                contactRepositoryInterface.getBriefContactById(it.contactId)
                        if (briefContact != null) {
                            LocatedContact(
                                    id = it.contactId,
                                    name = briefContact.name,
                                    photoUri = briefContact.photoUri,
                                    latitude = it.latitude,
                                    longitude = it.longitude,
                                    address = it.address
                            )
                        }
                        // Если для текущего идентификатора не найден контакт, это говорит
                        // о том, что этот контакт вручную удален пользователем за рамками
                        // нашего приложения. Такие ложные элементы в список не вносить,
                        // а из БД удалить.
                        else {
                            deleteLocationDataById(it.contactId)
                            Log.d(TAG, "ContactLocationRepository: из БД удален "
                                    + "несуществующий контакт id = ${it.contactId}"
                                    + " address = ${it.address}")
                            null
                        }
                    }
                }
            }

    override suspend fun addContactLocation(locatedContact: LocatedContact) {
        withContext(Dispatchers.IO) {
            db.contactLocationDao().insertContactLocation(locatedContact.toDatabase())
        }
    }

    override suspend fun updateContactLocation(locatedContact: LocatedContact) {
        withContext(Dispatchers.IO) {
            db.contactLocationDao().updateContactLocation(locatedContact.toDatabase())
        }
    }

    override suspend fun deleteLocationDataById(contactId: String) {
        withContext(Dispatchers.IO) {
            db.contactLocationDao().deleteContactLocationById(contactId)
        }
    }

}
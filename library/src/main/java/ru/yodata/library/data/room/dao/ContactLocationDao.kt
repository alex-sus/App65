package ru.yodata.library.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import ru.yodata.library.data.room.entity.ContactLocationEntity


@Dao
interface ContactLocationDao {

    @Insert
    suspend fun insertContactLocation(contactLocationEntity: ContactLocationEntity)

    @Query("SELECT * FROM contact_locations_table WHERE contactId == :id")
    suspend fun getContactLocationById(id: String): ContactLocationEntity?

    @Query("SELECT * FROM contact_locations_table")
    suspend fun getAllContactLocations(): List<ContactLocationEntity>?

    @Query("SELECT COUNT(contactId) FROM contact_locations_table")
    suspend fun getRowCount(): Int

    @Update
    suspend fun updateContactLocation(contactLocationEntity: ContactLocationEntity)

    @Query("DELETE FROM contact_locations_table WHERE contactId == :id")
    suspend fun deleteContactLocationById(id: String)

}
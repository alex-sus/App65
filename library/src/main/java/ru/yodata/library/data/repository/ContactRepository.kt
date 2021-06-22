package ru.yodata.library.data.repository

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.yodata.java.entities.BriefContact
import ru.yodata.java.entities.Contact
import ru.yodata.java.interactors.ContactRepositoryInterface
import ru.yodata.library.utils.Constants.EMPTY_VALUE
import ru.yodata.library.utils.Constants.TAG
import java.util.*

private const val CUR_CONTACT_PHONE_SELECTION = "${ContactsContract.Data.MIMETYPE} = " +
        "'${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'" +
        " and ${ContactsContract.Data.LOOKUP_KEY} = ?"
private const val CUR_CONTACT_SELECTION = "${ContactsContract.Contacts.LOOKUP_KEY} = ?"
private const val CUR_CONTACT_DATA_SELECTION = "${ContactsContract.Data.LOOKUP_KEY} = ?"
private const val ALPHABET_SORT_ORDER = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
private val BRIEF_CONTACTS_PROJECTION = arrayOf(
        ContactsContract.Contacts.LOOKUP_KEY,
        ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.PHOTO_URI
)
private const val CURSOR_LOOKUP_KEY_COLUMN = 0
private const val CURSOR_DISPLAY_NAME_COLUMN = 1
private const val CURSOR_PHOTO_URI_COLUMN = 2
private val PHONE_PROJECTION = arrayOf(
        ContactsContract.Data.DATA1
)
private const val CURSOR_PHONE_COLUMN = 0
private val DETAIL_PROJECTION = arrayOf(
        ContactsContract.Data.MIMETYPE,
        ContactsContract.Data.DATA1,
        ContactsContract.Data.DATA2
)
private const val CURSOR_MIMETYPE_COLUMN = 0
private const val CURSOR_MAIN_VALUE_COLUMN = 1
private const val CURSOR_ADDITIONAL_VALUE_COLUMN = 2

class ContactRepository(private val contResolver: ContentResolver) : ContactRepositoryInterface {

    override suspend fun getContactList(): List<BriefContact> =
            withContext(Dispatchers.IO) {
                val briefContactList = mutableListOf<BriefContact>()
                contResolver.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        BRIEF_CONTACTS_PROJECTION,
                        null,
                        null,
                        ALPHABET_SORT_ORDER
                )?.use { cursor ->
                    Log.d(TAG, "Создаю список контактов")
                    if (cursor.moveToFirst()) {
                        do {
                            briefContactList.add(
                                    BriefContact(
                                            id = cursor.getString(CURSOR_LOOKUP_KEY_COLUMN),
                                            name = cursor.getString(CURSOR_DISPLAY_NAME_COLUMN),
                                            phone = EMPTY_VALUE,
                                            photoUri = cursor.getString(CURSOR_PHOTO_URI_COLUMN)
                                    )
                            )
                            Log.d(TAG, "Контакт: ${cursor.getString(CURSOR_DISPLAY_NAME_COLUMN)}")
                        } while (cursor.moveToNext())
                    }
                }
                // Номера телефонов у контактов находятся в другой таблице (ContactsContract.Data) -
                // найти их там и внести в список.
                briefContactList
                        .map { contact ->
                            launch {
                                val phones = mutableListOf<String>()
                                contResolver.query(
                                        ContactsContract.Data.CONTENT_URI,
                                        PHONE_PROJECTION,
                                        CUR_CONTACT_PHONE_SELECTION,
                                        arrayOf(contact.id),
                                        null
                                )?.use { cursor ->
                                    if (cursor.moveToFirst()) {
                                        do {
                                            phones.add(cursor.getString(CURSOR_PHONE_COLUMN))
                                        } while (cursor.moveToNext())
                                        contact.phone = phones.getOrNull(0) ?: EMPTY_VALUE
                                        // contact = contact.copy(phone = phones.getOrNull(0) ?: EMPTY_VALUE)
                                        phones.clear()
                                    }
                                }
                            }
                        }
                        .joinAll()
                return@withContext briefContactList
            }

    override suspend fun getBriefContactById(contactId: String): BriefContact? =
            withContext(Dispatchers.IO) {
                contResolver.query(
                        ContactsContract.Contacts.CONTENT_URI,
                        BRIEF_CONTACTS_PROJECTION,
                        CUR_CONTACT_SELECTION,
                        arrayOf(contactId),
                        null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        BriefContact(
                            id = cursor.getString(CURSOR_LOOKUP_KEY_COLUMN),
                            name = cursor.getString(CURSOR_DISPLAY_NAME_COLUMN),
                            phone = EMPTY_VALUE,
                            photoUri = cursor.getString(CURSOR_PHOTO_URI_COLUMN)
                        )
                    } else null
                }
            }

    override suspend fun getContactById(contactId: String): Flow<Contact> =
        flow {
            //withContext(Dispatchers.IO) {
            var name = EMPTY_VALUE
            var birthday: Calendar? = null
            val phones = mutableListOf<String>()
            val emails = mutableListOf<String>()
            var description = EMPTY_VALUE
            var bigPhotoUri: String? = null
            contResolver.query(
                ContactsContract.Data.CONTENT_URI,
                DETAIL_PROJECTION,
                CUR_CONTACT_DATA_SELECTION,
                arrayOf(contactId),
                null
            )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        do {
                            when (cursor.getString(CURSOR_MIMETYPE_COLUMN)) {
                                ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE -> {
                                    birthday = getContactBirthday(cursor)
                                }
                                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                                    addContactPhone(cursor, phones)
                                }
                                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
                                    addContactEmail(cursor, emails)
                                }
                                ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE -> {
                                    description = getContactDescription(cursor)
                                }
                            }
                        } while (cursor.moveToNext())
                    }
                }
                // Получить имя контакта и ссылку на фото (URI) из таблицы ContactsContract.Contacts
                contResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    BRIEF_CONTACTS_PROJECTION,
                    CUR_CONTACT_SELECTION,
                    arrayOf(contactId),
                    null
                )?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        name = cursor.getString(CURSOR_DISPLAY_NAME_COLUMN)
                        bigPhotoUri = cursor.getString(CURSOR_PHOTO_URI_COLUMN)
                    }
                }
            emit(
                Contact(
                    id = contactId,
                    name = name,
                    birthday = birthday,
                    phone1 = phones.getOrNull(0) ?: EMPTY_VALUE,
                    phone2 = phones.getOrNull(1) ?: EMPTY_VALUE,
                    email1 = emails.getOrNull(0) ?: EMPTY_VALUE,
                    email2 = emails.getOrNull(1) ?: EMPTY_VALUE,
                    description = description,
                    bigPhotoUri = bigPhotoUri
                )
            )
            // }
        }.flowOn(Dispatchers.IO)

    private fun getContactBirthday(cursor: Cursor): Calendar? =
        if (cursor.getInt(CURSOR_ADDITIONAL_VALUE_COLUMN) ==
            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY
        ) {
            cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                .split("-")
                .let { date ->
                    Calendar.getInstance().apply {
                        set(Calendar.DAY_OF_MONTH, date[2].toInt())
                        set(Calendar.MONTH, date[1].toInt() - 1)
                        set(Calendar.YEAR, date[0].toInt())
                    }
                }
        } else null

    private fun addContactPhone(cursor: Cursor, phones: MutableList<String>) {
        phones.add(
            cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                ?: EMPTY_VALUE
        )
    }

    private fun addContactEmail(cursor: Cursor, emails: MutableList<String>) {
        emails.add(
            cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                ?: EMPTY_VALUE
        )
    }

    private fun getContactDescription(cursor: Cursor): String =
        cursor.getString(CURSOR_MAIN_VALUE_COLUMN) ?: EMPTY_VALUE

}
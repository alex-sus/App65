package ru.yodata.library.data

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import kotlinx.coroutines.Dispatchers
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

    override suspend fun getContactList(): List<BriefContact> {
        val briefContactList = mutableListOf<BriefContact>()
        withContext(Dispatchers.IO) {
            Log.d(TAG, "Старт метода: ${this::class.java.simpleName}:" +
                    "${object {}.javaClass.enclosingMethod.name}")
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
                                    phones.clear()
                                }
                            }
                        }
                    }
                    .joinAll()
        }
        return briefContactList
    }

    override suspend fun getContactById(contactId: String): Contact {
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
            Log.d(TAG, "Получение данных текущего контакта. Строк: ${cursor.count}")
            if (cursor.moveToFirst()) {
                do {
                    Log.d(TAG, "Данные текущего контакта: " +
                            cursor.getString(CURSOR_MIMETYPE_COLUMN))
                    when (cursor.getString(CURSOR_MIMETYPE_COLUMN)) {
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE -> {
                            name = cursor.getString(CURSOR_MAIN_VALUE_COLUMN) ?: EMPTY_VALUE
                        }
                        ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE -> {
                            if (cursor.getInt(CURSOR_ADDITIONAL_VALUE_COLUMN) ==
                                    ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY) {
                                cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                                        .split("-")
                                        .let { date ->
                                            birthday = Calendar.getInstance().apply {
                                                set(Calendar.DAY_OF_MONTH, date[2].toInt())
                                                set(Calendar.MONTH, date[1].toInt() - 1)
                                                set(Calendar.YEAR, date[0].toInt())
                                            }
                                        }
                            }
                        }
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE -> {
                            phones.add(
                                cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                                    ?: EMPTY_VALUE
                            )
                        }
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
                            emails.add(
                                    cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                                            ?: EMPTY_VALUE
                            )
                        }
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE -> {
                            description = cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                                    ?: EMPTY_VALUE
                        }
                    }
                } while (cursor.moveToNext())
                Log.d(TAG, "Получение данных текущего контакта окончено")
            }
        }
        // Получить ссылку на фото (URI) из таблицы ContactsContract.Contacts
        contResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                BRIEF_CONTACTS_PROJECTION,
                CUR_CONTACT_SELECTION,
                arrayOf(contactId),
                null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                bigPhotoUri = cursor.getString(CURSOR_PHOTO_URI_COLUMN)
            }
        }
        return Contact(
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
    }
}
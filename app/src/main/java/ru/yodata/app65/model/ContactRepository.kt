package ru.yodata.app65.model

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import ru.yodata.app65.utils.Constants.SHOW_EMPTY_VALUE
import ru.yodata.app65.utils.Constants.TAG
import java.util.*

object ContactRepository {

    private const val CUR_CONTACT_PHONE_SELECTION = "${ContactsContract.Data.MIMETYPE} = " +
            "'${ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE}'" +
            " and ${ContactsContract.Data.LOOKUP_KEY} = ?"
    private const val CUR_CONTACT_DATA_SELECTION = "${ContactsContract.Data.LOOKUP_KEY} = ?"
    private const val ALPHABET_SORT_ORDER = "${ContactsContract.Contacts.DISPLAY_NAME} ASC"
    private val BRIEF_CONTACTS_PROJECTION = arrayOf(
            ContactsContract.Contacts.LOOKUP_KEY,
            ContactsContract.Contacts.DISPLAY_NAME,
    )
    private const val CURSOR_LOOKUP_KEY_COLUMN = 0
    private const val CURSOR_DISPLAY_NAME_COLUMN = 1
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

    fun getContactList(contResolver: ContentResolver): List<BriefContact> {
        val briefContactList = mutableListOf<BriefContact>()
        Log.d(TAG, "Старт метода: ${this::class.java.simpleName}:" +
                "${object {}.javaClass.getEnclosingMethod().getName()}")
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
                    briefContactList.add(BriefContact(
                            id = cursor.getString(CURSOR_LOOKUP_KEY_COLUMN),
                            name = cursor.getString(CURSOR_DISPLAY_NAME_COLUMN),
                            phone = SHOW_EMPTY_VALUE
                    ))
                    Log.d(TAG, "Контакт: ${cursor.getString(CURSOR_DISPLAY_NAME_COLUMN)}")
                } while (cursor.moveToNext())
            }
            // Номера телефонов у контактов находятся в другой таблице (ContactsContract.Data) -
            // найти их там и внести в список.
            val phones = mutableListOf<String>()
            for (contact in briefContactList) {
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
                        contact.phone = phones[0]
                        phones.clear()
                    }
                }
            }
        }
        return briefContactList
    }

    fun getContactById(contResolver: ContentResolver, contactId: String): Contact {
        var name = SHOW_EMPTY_VALUE
        var birthday: Calendar? = null
        val phones = mutableListOf<String>()
        val emails = mutableListOf<String>()
        var description = SHOW_EMPTY_VALUE
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
                            name = cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
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
                            phones.add(cursor.getString(CURSOR_MAIN_VALUE_COLUMN))
                        }
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE -> {
                            emails.add(cursor.getString(CURSOR_MAIN_VALUE_COLUMN))
                        }
                        ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE -> {
                            description = cursor.getString(CURSOR_MAIN_VALUE_COLUMN)
                                    ?: SHOW_EMPTY_VALUE
                        }
                    }
                } while (cursor.moveToNext())
                Log.d(TAG, "Получение данных текущего контакта окончено")
            }
        }
        return Contact(
                id = contactId,
                name = name,
                birthday = birthday,
                phone1 = phones.getOrNull(0) ?: SHOW_EMPTY_VALUE,
                phone2 = phones.getOrNull(1) ?: SHOW_EMPTY_VALUE,
                email1 = emails.getOrNull(0) ?: SHOW_EMPTY_VALUE,
                email2 = emails.getOrNull(1) ?: SHOW_EMPTY_VALUE,
                description = description
        )
    }
}
package ru.yodata.library.view.contact

import ru.yodata.java.entities.Contact
import ru.yodata.java.entities.LocationData

data class ContactDetailsState(
    val contactDetails: Contact?,
    val locationData: LocationData?
)

fun emptyState() =
    ContactDetailsState(
        contactDetails = null,
        locationData = null
    )
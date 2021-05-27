package ru.yodata.java.entities

data class LocatedContact(
        val id: String,
        val name: String,
        val photoUri: String?,
        val latitude: Double,
        val longitude: Double,
        val address: String
) {
        override fun toString(): String {
                return name
        }

        /*fun LocatedContact.toDatabase(locatedContact: LocatedContact)
                : ContactLocationEntity =
                with(locatedContact) {
                        ContactLocationEntity(
                                contactId = id,
                                latitude = latitude,
                                longitude = longitude,
                                address = address
                        )
                }*/
}
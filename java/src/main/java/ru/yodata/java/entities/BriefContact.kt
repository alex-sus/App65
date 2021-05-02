package ru.yodata.java.entities

data class BriefContact(
        val id: String,
        val name: String,
        var phone: String,
        val photoUri: String?
)

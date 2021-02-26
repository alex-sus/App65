package ru.yodata.app65.view

// Интерфейс нужен для навигации между фрагментами
interface OnContactListCallback {

    fun navigateToContactDetailsFragment(contactId: Int)

}
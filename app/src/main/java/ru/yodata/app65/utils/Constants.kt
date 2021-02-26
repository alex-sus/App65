package ru.yodata.app65.utils

import ru.yodata.app65.model.MyContact

object Constants {

    val contactList: List<MyContact> = listOf(MyContact(
            id = "1",
            name = "Суслопаров Алексей Владимирович",
            phone1 = "+7-909-111-22-33",
            phone2 = "(3412)77-88-99",
            email1 = "one@gmail.com",
            email2 = "two@gmail.com",
            description = "Стиль — это один или несколько сгруппированных атрибутов " +
                "форматирования, которые отвечают за внешний вид и поведение элементов или окна. " +
                "Стиль может задавать такие свойства, как ширину, отступы, цвет текста, " +
                "размер шрифта, цвет фона и так далее.",
            photo = ""
        )
    )

}
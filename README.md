# App65
25/02/2021 Домашнее задание №1 - Создан проект с пустой activity.

05/03/2021 Домашнее задание №2 - Созданы 1 activity и 2 фрагмента: "Список контактов" и "Детали
           контакта".

12/03/2021 Домашнее задание №3 - Создан привязанный сервис с асинхронными методами для получения
            данных контактов.

19/03/2021 Домашнее задание №4 - У контакта создано дополнительное поле "День рождения" с кнопкой
            для включения и выключения уведомлений о нем с использованием Alarm Manager,
            Broadcast Receiver и выводом уведомлений на экран через Notification, позволяющих
            открыть детальную карточку контакта, у которого сегодня день рождения.

29/03/2021 Домашнее задание №5 - Добавлена обработка запроса разрешений (permissions), в методе
            загрузки списка контактов реализовано получение списка всех контактов пользователя из
            поставщика контактов, в методе загрузки деталей контакта по ID - реализовано получение
            всех необходимых данных контакта из поставщика контактов по ID.

04/04/2021 Домашнее задание №6 - Произведен рефакторинг на MVVM. Контакты грузятся  из
            поставщика контактов через репозиторий, обращение к привязанному сервису убрано.
package ru.yodata.app65

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import ru.yodata.app65.utils.Constants.ANDROID_RESOURCE
import ru.yodata.app65.utils.Constants.CHANNEL_ID
import ru.yodata.app65.utils.Constants.notificationSound

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        // Звук нотификации, привязка к ресурсу
        notificationSound = Uri.parse(ANDROID_RESOURCE + packageName +
                "/" + R.raw.sweet)
        // Создание канала нотификаций
        createNotificationChannel()
    }

    // Создание и регистрация канала нотификаций, без него нотификации не сработают на Android ver.8
    // и выше
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val descriptionText = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                setImportance(importance)
                description = descriptionText
                lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                setSound(notificationSound, audioAttributes)
                enableVibration(true)
                setShowBadge(true)
            }
            // Регистрация канала в системе
            val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}
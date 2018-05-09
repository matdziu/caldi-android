package com.caldi.notifications.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.support.annotation.RequiresApi
import com.caldi.R
import com.caldi.constants.ORGANIZER_CHANNEL_ID


class NotificationChannelsBuilder(private val context: Context) {

    @RequiresApi(26)
    fun buildChannels() {
        val notificationsManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val organizerChannel = NotificationChannel(ORGANIZER_CHANNEL_ID,
                context.getString(R.string.organizer_channel_title),
                NotificationManager.IMPORTANCE_HIGH)
        organizerChannel.enableLights(true)
        organizerChannel.enableVibration(true)
        organizerChannel.lightColor = Color.GREEN
        organizerChannel.importance = NotificationManager.IMPORTANCE_HIGH
        organizerChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        notificationsManager.createNotificationChannel(organizerChannel)
    }
}
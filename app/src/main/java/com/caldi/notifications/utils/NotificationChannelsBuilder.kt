package com.caldi.notifications.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.support.annotation.RequiresApi
import android.support.annotation.StringRes
import com.caldi.R
import com.caldi.constants.NEW_CONNECTION_CHANNEL_ID
import com.caldi.constants.ORGANIZER_CHANNEL_ID


class NotificationChannelsBuilder(private val context: Context) {

    @RequiresApi(26)
    fun buildChannels() {
        buildDefaultChannel(ORGANIZER_CHANNEL_ID, R.string.organizer_channel_title)
        buildDefaultChannel(NEW_CONNECTION_CHANNEL_ID, R.string.new_connection_channel_title)
    }

    @RequiresApi(26)
    private fun buildDefaultChannel(channelId: String, @StringRes channelTitleRes: Int) {
        val notificationsManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, context.getString(channelTitleRes),
                NotificationManager.IMPORTANCE_HIGH)
        channel.enableLights(true)
        channel.enableVibration(true)
        channel.lightColor = Color.GREEN
        channel.importance = NotificationManager.IMPORTANCE_HIGH
        channel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        notificationsManager.createNotificationChannel(channel)
    }
}
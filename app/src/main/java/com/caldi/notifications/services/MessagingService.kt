package com.caldi.notifications.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.caldi.R
import com.caldi.constants.ORGANIZER_CHANNEL_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_REQUEST_CODE
import com.caldi.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.data["title"]
        val body = remoteMessage.data["body"]
        showNotification(title, body)
    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, ORGANIZER_NOTIFICATION_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, ORGANIZER_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setContentTitle(title)
                .setContentText(body)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationsManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.notify(ORGANIZER_NOTIFICATION_ID, notification)
    }
}
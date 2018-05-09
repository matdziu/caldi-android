package com.caldi.notifications.services

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.caldi.R
import com.caldi.constants.ORGANIZER_CHANNEL_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_REQUEST_CODE
import com.caldi.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import io.reactivex.Observable
import java.util.concurrent.TimeUnit


class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title
        val body = remoteMessage.notification?.body
        showNotification(title, body)
    }

    private fun showNotification(title: String?, body: String?) {
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, ORGANIZER_NOTIFICATION_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, ORGANIZER_CHANNEL_ID)
                .setContentText(body)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setFullScreenIntent(pendingIntent, true)
                .build()

        val notificationsManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.notify(ORGANIZER_NOTIFICATION_ID, notification)
        Observable.timer(3000, TimeUnit.MILLISECONDS)
                .subscribe { notificationsManager.cancel(ORGANIZER_NOTIFICATION_ID) }
    }
}
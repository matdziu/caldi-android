package com.caldi.notifications.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.caldi.R
import com.caldi.constants.NEW_CONNECTION_CHANNEL_ID
import com.caldi.constants.NEW_CONNECTION_NOTIFICATION_ID
import com.caldi.constants.NEW_CONNECTION_NOTIFICATION_REQUEST_CODE
import com.caldi.constants.NEW_CONNECTION_NOTIFICATION_TYPE
import com.caldi.constants.NOTIFICATION_BODY_LOC_ARGS
import com.caldi.constants.NOTIFICATION_TITLE_LOC_ARGS
import com.caldi.constants.NOTIFICATION_TYPE_KEY
import com.caldi.constants.ORGANIZER_CHANNEL_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_REQUEST_CODE
import com.caldi.constants.ORGANIZER_NOTIFICATION_TYPE
import com.caldi.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray


class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val titleLocArgs = jsonStringToArray(remoteMessage.data[NOTIFICATION_TITLE_LOC_ARGS])
        val bodyLocArgs = jsonStringToArray(remoteMessage.data[NOTIFICATION_BODY_LOC_ARGS])
        val notificationType = remoteMessage.data[NOTIFICATION_TYPE_KEY]

        when (notificationType) {
            ORGANIZER_NOTIFICATION_TYPE -> handleOrganizerNotification(titleLocArgs, bodyLocArgs)
            NEW_CONNECTION_NOTIFICATION_TYPE -> handleNewConnectionNotification(titleLocArgs, bodyLocArgs)
        }
    }

    private fun handleOrganizerNotification(titleLocArgs: Array<String>, bodyLocArgs: Array<String>) {
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, ORGANIZER_NOTIFICATION_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val title = getString(R.string.organizer_message_notification_title, *titleLocArgs)
        val body = getString(R.string.organizer_message_notification_body, *bodyLocArgs)

        showDefaultNotification(pendingIntent, title, body, ORGANIZER_CHANNEL_ID, ORGANIZER_NOTIFICATION_ID)
    }

    private fun handleNewConnectionNotification(titleLocArgs: Array<String>, bodyLocArgs: Array<String>) {
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, NEW_CONNECTION_NOTIFICATION_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val title = getString(R.string.new_connection_notification_title, *titleLocArgs)
        val body = getString(R.string.new_connection_notification_body, *bodyLocArgs)

        showDefaultNotification(pendingIntent, title, body, NEW_CONNECTION_CHANNEL_ID, NEW_CONNECTION_NOTIFICATION_ID)
    }

    private fun showDefaultNotification(pendingIntent: PendingIntent,
                                        title: String,
                                        body: String,
                                        channelId: String,
                                        notificationId: Int) {
        val notification = NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(body)
                .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

        val notificationsManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.notify(notificationId, notification)
    }

    private fun jsonStringToArray(jsonString: String?): Array<String> {
        val stringArray = arrayListOf<String>()
        val jsonArray = JSONArray(jsonString)
        for (index in 0 until jsonArray.length()) {
            stringArray.add(jsonArray.getString(index))
        }
        return stringArray.toTypedArray()
    }
}
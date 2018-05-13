package com.caldi.notifications.services

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import com.caldi.CaldiApplication
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.CHAT_MESSAGE_CHANNEL_ID
import com.caldi.constants.CHAT_MESSAGE_NOTIFICATION_ID
import com.caldi.constants.CHAT_MESSAGE_NOTIFICATION_REQUEST_CODE
import com.caldi.constants.CHAT_MESSAGE_NOTIFICATION_TYPE
import com.caldi.constants.EXTRAS_EVENT_ID
import com.caldi.constants.NEW_CONNECTION_CHANNEL_ID
import com.caldi.constants.NEW_CONNECTION_NOTIFICATION_ID
import com.caldi.constants.NEW_CONNECTION_NOTIFICATION_REQUEST_CODE
import com.caldi.constants.NEW_CONNECTION_NOTIFICATION_TYPE
import com.caldi.constants.NOTIFICATION_BODY_LOC_ARGS
import com.caldi.constants.NOTIFICATION_EXTRAS_KEY
import com.caldi.constants.NOTIFICATION_TITLE_LOC_ARGS
import com.caldi.constants.NOTIFICATION_TYPE_KEY
import com.caldi.constants.ORGANIZER_CHANNEL_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_ID
import com.caldi.constants.ORGANIZER_NOTIFICATION_REQUEST_CODE
import com.caldi.constants.ORGANIZER_NOTIFICATION_TYPE
import com.caldi.extensions.jsonToArrayOfStrings
import com.caldi.extensions.jsonToMapOfStrings
import com.caldi.organizer.OrganizerActivity
import com.caldi.splash.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessagingService : FirebaseMessagingService() {

    private lateinit var caldiApplication: CaldiApplication

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        caldiApplication = application as CaldiApplication

        val titleLocArgs = remoteMessage.data[NOTIFICATION_TITLE_LOC_ARGS].jsonToArrayOfStrings()
        val bodyLocArgs = remoteMessage.data[NOTIFICATION_BODY_LOC_ARGS].jsonToArrayOfStrings()
        val extras = remoteMessage.data[NOTIFICATION_EXTRAS_KEY].jsonToMapOfStrings()
        val notificationType = remoteMessage.data[NOTIFICATION_TYPE_KEY]

        when (notificationType) {
            ORGANIZER_NOTIFICATION_TYPE -> handleOrganizerMessageNotification(titleLocArgs, bodyLocArgs, extras)
            NEW_CONNECTION_NOTIFICATION_TYPE -> handleNewConnectionNotification(titleLocArgs, bodyLocArgs, extras)
            CHAT_MESSAGE_NOTIFICATION_TYPE -> handleChatMessageNotification(titleLocArgs, bodyLocArgs, extras)
        }
    }

    private fun handleChatMessageNotification(titleLocArgs: Array<String>,
                                              bodyLocArgs: Array<String>,
                                              extras: Map<String, String>) {
        val intent = Intent(this, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, CHAT_MESSAGE_NOTIFICATION_REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val title = getString(R.string.chat_message_notification_title, *titleLocArgs)
        val body = getString(R.string.chat_message_notification_body, *bodyLocArgs)

        showDefaultNotification(pendingIntent, title, body, CHAT_MESSAGE_CHANNEL_ID, CHAT_MESSAGE_NOTIFICATION_ID)
    }

    private fun handleOrganizerMessageNotification(titleLocArgs: Array<String>,
                                                   bodyLocArgs: Array<String>,
                                                   extras: Map<String, String>) {
        if (caldiApplication.visibleActivity !is OrganizerActivity ||
                caldiApplication.visibleActivity is OrganizerActivity && extras[EXTRAS_EVENT_ID] != BaseDrawerActivity.eventId) {
            val intent = Intent(this, SplashActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, ORGANIZER_NOTIFICATION_REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val title = getString(R.string.organizer_message_notification_title, *titleLocArgs)
            val body = getString(R.string.organizer_message_notification_body, *bodyLocArgs)

            showDefaultNotification(pendingIntent, title, body, ORGANIZER_CHANNEL_ID, ORGANIZER_NOTIFICATION_ID)
        }
    }

    private fun handleNewConnectionNotification(titleLocArgs: Array<String>,
                                                bodyLocArgs: Array<String>,
                                                extras: Map<String, String>) {
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
}
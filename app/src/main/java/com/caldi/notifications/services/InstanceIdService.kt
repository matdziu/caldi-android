package com.caldi.notifications.services

import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import com.caldi.constants.NOTIFICATION_TOKEN_ACTION
import com.caldi.constants.NOTIFICATION_TOKEN_KEY
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        sendLocalBroadcastWithToken(FirebaseInstanceId.getInstance().token)
    }

    private fun sendLocalBroadcastWithToken(notificationToken: String?) {
        val intent = Intent(NOTIFICATION_TOKEN_ACTION)
        intent.putExtra(NOTIFICATION_TOKEN_KEY, notificationToken)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
}
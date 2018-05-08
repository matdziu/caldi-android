package com.caldi.notifications.services

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class InstanceIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val deviceToken = FirebaseInstanceId.getInstance().id
    }
}
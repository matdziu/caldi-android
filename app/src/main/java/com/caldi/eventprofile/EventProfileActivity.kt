package com.caldi.eventprofile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY

class EventProfileActivity : BaseDrawerActivity() {

    companion object {

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, EventProfileActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_event_profile)
        super.onCreate(savedInstanceState)
        setNavigationSelection(R.id.event_profile_item)

        val eventId = intent.getStringExtra(EVENT_ID_KEY)
    }
}
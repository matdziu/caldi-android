package com.caldi.meetpeople

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.EVENT_ID_KEY

class MeetPeopleActivity : BaseDrawerActivity() {

    companion object {

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, MeetPeopleActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_meet_people)
        super.onCreate(savedInstanceState)

        eventId = intent.getStringExtra(EVENT_ID_KEY)
        Toast.makeText(this, eventId, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.meet_people_item)
    }
}
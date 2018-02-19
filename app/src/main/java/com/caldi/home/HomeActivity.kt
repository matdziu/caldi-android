package com.caldi.home

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.home.list.EventsAdapter
import com.caldi.models.Event
import kotlinx.android.synthetic.main.activity_home.eventsRecyclerView

class HomeActivity : BaseDrawerActivity() {

    private val eventsAdapter: EventsAdapter = EventsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_home)
        super.onCreate(savedInstanceState)
        setNavigationSelection(R.id.events_item)

        eventsRecyclerView.adapter = eventsAdapter
        eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        eventsAdapter.update(arrayListOf(Event("1", "AndroidCan 2017", "http://droidcon.pl/assets/images/droidcon-logo-simple.4cf93860f5dbc7d1a3ac36bbc6e498a8.png")))
    }
}
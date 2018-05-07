package com.caldi.home.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.R
import com.caldi.home.models.Event
import com.caldi.injection.modules.GlideApp
import com.caldi.organizer.OrganizerActivity
import kotlinx.android.synthetic.main.item_event.view.eventImageView
import kotlinx.android.synthetic.main.item_event.view.eventTextView

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindEvent(event: Event) {
        with(itemView) {
            eventTextView.text = event.name
            GlideApp.with(this)
                    .load(event.imageUrl)
                    .placeholder(R.drawable.event_placeholder)
                    .into(eventImageView)
            setOnClickListener { OrganizerActivity.start(context, event.id) }
        }
    }
}
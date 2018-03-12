package com.caldi.home.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.R
import com.caldi.eventprofile.EventProfileActivity
import com.caldi.home.models.Event
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_event.view.eventImageView
import kotlinx.android.synthetic.main.item_event.view.eventTextView

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindEvent(event: Event) {
        with(itemView) {
            eventTextView.text = event.name
            Picasso.get()
                    .load(event.imageUrl)
                    .placeholder(R.drawable.event_placeholder)
                    .into(eventImageView)
            setOnClickListener { EventProfileActivity.start(context, event.id) }
        }
    }
}
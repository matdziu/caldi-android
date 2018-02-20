package com.caldi.home.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.caldi.home.models.Event
import kotlinx.android.synthetic.main.item_event.view.eventImageView
import kotlinx.android.synthetic.main.item_event.view.eventTextView

class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindEvent(event: Event) {
        with(itemView) {
            eventTextView.text = event.name
            Glide.with(itemView).load(event.imageUrl).into(eventImageView)
        }
    }
}
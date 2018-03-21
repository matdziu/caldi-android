package com.caldi.home.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.home.models.Event

class EventsAdapter : RecyclerView.Adapter<EventViewHolder>() {

    private val eventList = arrayListOf<Event>()

    override fun getItemCount(): Int = eventList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bindEvent(eventList[position])
    }

    fun setEventList(eventList: List<Event>) {
        this.eventList.clear()
        this.eventList.addAll(eventList)
        notifyDataSetChanged()
    }
}
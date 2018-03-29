package com.caldi.organizer.list

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.organizer.models.Message
import com.caldi.organizer.utils.MessageItemDiffCallback

class MessagesAdapter : ListAdapter<Message, MessageViewHolder>(MessageItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_organizer_message, parent, false)
        return MessageViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun getLastTimestamp(): String = getItem(0).timestamp
}
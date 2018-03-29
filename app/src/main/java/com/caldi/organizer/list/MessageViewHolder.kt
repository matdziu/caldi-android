package com.caldi.organizer.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.organizer.models.Message
import kotlinx.android.synthetic.main.item_organizer_message.view.organizerMessageTextView

class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(message: Message) {
        with(itemView) {
            organizerMessageTextView.text = message.message
        }
    }
}
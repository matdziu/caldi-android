package com.caldi.chat.list

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_received_message.view.receivedMessageTextView

class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(messageViewState: MessageViewState) {
        with(messageViewState) {
            itemView.receivedMessageTextView.text = message
        }
    }
}
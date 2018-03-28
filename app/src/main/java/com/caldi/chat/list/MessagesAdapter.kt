package com.caldi.chat.list

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldi.R

class MessagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class MessageType { SENT, RECEIVED }

    private val messagesViewStateList = arrayListOf<MessageViewState>()

    override fun getItemViewType(position: Int): Int {
        return when (messagesViewStateList[position].isOwn) {
            true -> MessageType.SENT.ordinal
            else -> MessageType.RECEIVED.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (MessageType.values()[viewType]) {
            MessageType.SENT -> SentMessageViewHolder(inflateItemView(R.layout.item_sent_message, parent))
            MessageType.RECEIVED -> ReceivedMessageViewHolder(inflateItemView(R.layout.item_received_message, parent))
        }
    }

    private fun inflateItemView(@LayoutRes resId: Int, parent: ViewGroup): View {
        return LayoutInflater.from(parent.context).inflate(resId, parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessageViewState = messagesViewStateList[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(currentMessageViewState)
            is ReceivedMessageViewHolder -> holder.bind(currentMessageViewState)
        }
    }

    override fun getItemCount(): Int = messagesViewStateList.size
}
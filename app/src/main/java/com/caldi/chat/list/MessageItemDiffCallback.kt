package com.caldi.chat.list

import android.support.v7.util.DiffUtil

class MessageItemDiffCallback : DiffUtil.ItemCallback<MessageViewState>() {

    override fun areItemsTheSame(oldItem: MessageViewState, newItem: MessageViewState): Boolean {
        return oldItem.messageId == newItem.messageId
    }

    override fun areContentsTheSame(oldItem: MessageViewState, newItem: MessageViewState): Boolean {
        return oldItem == newItem
    }
}
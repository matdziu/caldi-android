package com.caldi.chatlist.utils

import android.support.v7.util.DiffUtil
import com.caldi.chatlist.models.ChatItem

class ChatItemDiffCallback : DiffUtil.ItemCallback<ChatItem>() {

    override fun areItemsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return oldItem.chatId == newItem.chatId
    }

    override fun areContentsTheSame(oldItem: ChatItem, newItem: ChatItem): Boolean {
        return oldItem == newItem
    }
}
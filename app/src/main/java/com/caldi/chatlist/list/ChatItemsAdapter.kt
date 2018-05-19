package com.caldi.chatlist.list

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.chatlist.models.ChatItem
import com.caldi.chatlist.utils.ChatItemDiffCallback

class ChatItemsAdapter(private val eventId: String)
    : ListAdapter<ChatItem, ChatItemViewHolder>(ChatItemDiffCallback()) {

    var currentChatItemList = listOf<ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(currentChatItemList[position], eventId)
    }

    fun addChatItemsBatch(chatItemList: List<ChatItem>) {
        val newList = currentChatItemList + chatItemList
        submitList(newList)
        currentChatItemList = newList
    }
}
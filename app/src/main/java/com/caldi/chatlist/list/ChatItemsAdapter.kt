package com.caldi.chatlist.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.chatlist.models.ChatItem

class ChatItemsAdapter : RecyclerView.Adapter<ChatItemViewHolder>() {

    private val chatItemList = arrayListOf<ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(chatItemList[position])
    }

    override fun getItemCount(): Int = chatItemList.size
}
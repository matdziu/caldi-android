package com.caldi.chatlist.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.chatlist.models.ChatItem

class ChatItemsAdapter(private val eventId: String) : RecyclerView.Adapter<ChatItemViewHolder>() {

    private val chatItemList = arrayListOf<ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return ChatItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(chatItemList[position], eventId)
    }

    override fun getItemCount(): Int = chatItemList.size

    fun setChatItemList(chatItemList: List<ChatItem>) {
        if (chatItemList.isNotEmpty() && this.chatItemList != chatItemList) {
            this.chatItemList.clear()
            this.chatItemList.addAll(chatItemList)
            notifyDataSetChanged()
        }
    }
}
package com.caldi.chat.list

import android.support.v7.util.DiffUtil

class MessagesDiffCallback(private val oldMessagesList: List<MessageViewState>,
                           private val newMessagesList: List<MessageViewState>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldMessagesList.size

    override fun getNewListSize(): Int = newMessagesList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMessagesList[oldItemPosition].messageId == newMessagesList[newItemPosition].messageId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldMessagesList[oldItemPosition] == newMessagesList[newItemPosition]
    }
}
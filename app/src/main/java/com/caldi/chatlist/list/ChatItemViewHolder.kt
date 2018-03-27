package com.caldi.chatlist.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.R
import com.caldi.chat.ChatActivity
import com.caldi.chatlist.models.ChatItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_chat.view.chatItemImageView
import kotlinx.android.synthetic.main.item_chat.view.chatItemTextView

class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(chatItem: ChatItem) {
        with(itemView) {
            chatItemTextView.text = chatItem.name
            Picasso.get()
                    .load(chatItem.imageUrl)
                    .placeholder(R.drawable.profile_picture_shape)
                    .into(chatItemImageView)
            setOnClickListener { ChatActivity.start(context, chatItem.chatId, chatItem.name, chatItem.imageUrl) }
        }
    }
}
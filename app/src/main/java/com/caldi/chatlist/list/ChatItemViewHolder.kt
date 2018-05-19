package com.caldi.chatlist.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.R
import com.caldi.chat.ChatActivity
import com.caldi.chatlist.models.ChatItem
import com.caldi.injection.modules.GlideApp
import kotlinx.android.synthetic.main.item_chat.view.chatItemImageView
import kotlinx.android.synthetic.main.item_chat.view.chatItemTextView
import kotlinx.android.synthetic.main.item_chat.view.unreadIndicatorImage

class ChatItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(chatItem: ChatItem, eventId: String) {
        with(itemView) {
            chatItemTextView.text = chatItem.name
            loadProfilePictureUrl(chatItem.imageUrl, itemView)
            showUnreadIndicatorImage(chatItem.unread)
            setOnClickListener { ChatActivity.start(context, chatItem, eventId) }
        }
    }

    private fun showUnreadIndicatorImage(unread: Boolean) {
        if (unread) {
            itemView.unreadIndicatorImage.visibility = View.VISIBLE
        } else {
            itemView.unreadIndicatorImage.visibility = View.GONE
        }
    }

    private fun loadProfilePictureUrl(imageUrl: String, itemView: View) {
        if (imageUrl.isNotEmpty()) {
            GlideApp.with(itemView)
                    .load(imageUrl)
                    .placeholder(R.drawable.profile_picture_shape)
                    .into(itemView.chatItemImageView)
        } else {
            GlideApp.with(itemView)
                    .load(R.drawable.profile_picture_shape)
                    .into(itemView.chatItemImageView)
        }
    }
}
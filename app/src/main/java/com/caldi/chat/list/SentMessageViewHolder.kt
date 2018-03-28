package com.caldi.chat.list

import android.support.annotation.DrawableRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.R
import kotlinx.android.synthetic.main.item_sent_message.view.sentMessageTextView

class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(messageViewState: MessageViewState) {
        with(messageViewState) {
            itemView.sentMessageTextView.text = message
            if (isSent) setMessageBackground(R.drawable.round_empty_green_background)
            else setMessageBackground(R.drawable.round_full_dim_green_background)
        }
    }

    private fun setMessageBackground(@DrawableRes resId: Int) {
        itemView.sentMessageTextView.background =
                ContextCompat.getDrawable(itemView.context, resId)
    }
}
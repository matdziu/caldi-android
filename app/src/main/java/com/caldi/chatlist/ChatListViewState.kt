package com.caldi.chatlist

import com.caldi.chatlist.models.ChatItem

data class ChatListViewState(val progress: Boolean = false,
                             val error: Boolean = false,
                             val dismissToast: Boolean = false,
                             val chatItemList: List<ChatItem> = listOf())
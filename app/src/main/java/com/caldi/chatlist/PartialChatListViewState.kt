package com.caldi.chatlist

import com.caldi.chatlist.models.ChatItem

sealed class PartialChatListViewState {

    class SuccessfulChatListBatchFetch(val chatList: List<ChatItem>) : PartialChatListViewState()

    class ProgressState : PartialChatListViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialChatListViewState()

    class ChatItemListenerRemoved : PartialChatListViewState()

    class ChatItemChanged(val chatItem: ChatItem) : PartialChatListViewState()
}
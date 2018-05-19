package com.caldi.chatlist

import com.caldi.chatlist.models.ChatItem

sealed class PartialChatListViewState {

    class SuccessfulChatListFetch(val chatList: List<ChatItem>) : PartialChatListViewState()

    class ProgressState : PartialChatListViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialChatListViewState()
}
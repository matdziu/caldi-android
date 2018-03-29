package com.caldi.chat

import com.caldi.chat.list.MessageViewState

data class ChatViewState(val messagesList: List<MessageViewState> = listOf(),
                         val error: Boolean = false,
                         val dismissToast: Boolean = false)
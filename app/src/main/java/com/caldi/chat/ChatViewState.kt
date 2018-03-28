package com.caldi.chat

import com.caldi.chat.list.MessageViewState

data class ChatViewState(val itemProgress: Boolean = false,
                         val error: Boolean = false,
                         val messagesList: List<MessageViewState> = listOf(),
                         val dismissToast: Boolean = false)
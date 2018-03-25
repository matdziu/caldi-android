package com.caldi.chat

import com.caldi.chat.models.Message

data class ChatViewState(val itemProgress: Boolean = false,
                         val error: Boolean = false,
                         val newMessage: Message = Message(),
                         val messagesBatchList: List<Message> = listOf(),
                         val dismissToast: Boolean = false)
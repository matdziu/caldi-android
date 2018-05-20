package com.caldi.chat

import com.caldi.chat.list.MessageViewState
import com.caldi.common.states.PersonProfileViewState

data class ChatViewState(val messagesList: List<MessageViewState> = listOf(),
                         val receiverProfile: PersonProfileViewState = PersonProfileViewState(),
                         val progress: Boolean = false,
                         val error: Boolean = false,
                         val dismissToast: Boolean = false)
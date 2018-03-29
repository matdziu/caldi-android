package com.caldi.chat

import com.caldi.chat.models.Message

sealed class PartialChatViewState {

    class MessagesListChanged(val updatedMessagesList: List<Message>) : PartialChatViewState()

    class ProgressState : PartialChatViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialChatViewState()

    class NewMessagesListenerRemoved : PartialChatViewState()
}
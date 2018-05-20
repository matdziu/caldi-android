package com.caldi.chat

import com.caldi.chat.models.Message
import com.caldi.common.states.PersonProfileViewState

sealed class PartialChatViewState {

    class MessagesListChanged(val updatedMessagesList: List<Message>) : PartialChatViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialChatViewState()

    class NewMessagesListenerRemoved : PartialChatViewState()

    class MessagesSetAsRead : PartialChatViewState()

    class ReceiverProfileFetchedState(val personProfileViewState: PersonProfileViewState) : PartialChatViewState()
}
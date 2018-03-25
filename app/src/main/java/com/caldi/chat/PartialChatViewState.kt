package com.caldi.chat

import com.caldi.chat.models.Message

sealed class PartialChatViewState {

    class MessageSendingSuccess : PartialChatViewState()

    class NewMessageAdded(val newMessage: Message) : PartialChatViewState()

    class NewMessagesListenerRemoved : PartialChatViewState()

    class MessagesBatchFetchSuccess(val messagesBatchList: List<Message>) : PartialChatViewState()

    class ItemProgressState : PartialChatViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialChatViewState()
}
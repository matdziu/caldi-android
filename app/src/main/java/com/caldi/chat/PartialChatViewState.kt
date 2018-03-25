package com.caldi.chat

import com.caldi.chat.models.Message

sealed class PartialChatViewState {

    class MessageSendingSuccess(val messageId: String) : PartialChatViewState()

    class NewMessageAdded(val newMessage: Message) : PartialChatViewState()

    class MessagesBatchFetchSuccess(val messagesBatchList: List<Message>) : PartialChatViewState()

    class ProgressState : PartialChatViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialChatViewState()
}
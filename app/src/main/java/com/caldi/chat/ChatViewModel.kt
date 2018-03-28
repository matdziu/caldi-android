package com.caldi.chat

import android.arch.lifecycle.ViewModel
import com.caldi.chat.list.MessageViewState
import com.caldi.chat.models.Message
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel(private val chatInteractor: ChatInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialChatViewState>()

    fun bind(chatView: ChatView, chatId: String) {
        val newMessagesListeningToggleObservable = chatView.emitNewMessagesListeningToggle()
                .flatMap {
                    if (it) chatInteractor.listenForNewMessages(chatId)
                    else chatInteractor.stopListeningForNewMessages(chatId)
                }

        val batchFetchTriggerObservable = chatView.emitBachFetchTrigger()
                .flatMap {
                    chatInteractor.fetchChatMessagesBatch(chatId, it)
                            .startWith(PartialChatViewState.ItemProgressState())
                }

        val sentMessageObservable = chatView.emitSentMessage()
                .flatMap { chatInteractor.sendMessage(it, chatId) }

        val mergedObservable = Observable.merge(arrayListOf(
                newMessagesListeningToggleObservable,
                batchFetchTriggerObservable,
                sentMessageObservable))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.scan(ChatViewState(), this::reduce).subscribe { chatView.render(it) })
    }

    private fun reduce(previousState: ChatViewState, partialState: PartialChatViewState)
            : ChatViewState {
        return when (partialState) {
            is PartialChatViewState.MessageSendingStarted -> previousState.copy(
                    messagesList = previousState.messagesList + convertToMessageViewState(partialState.message, false)
            )
            is PartialChatViewState.NewMessageAdded -> previousState.copy(
                    messagesList = concatMessagesList(previousState.messagesList,
                            listOf(convertToMessageViewState(partialState.newMessage))))
            is PartialChatViewState.NewMessagesListenerRemoved -> previousState
            is PartialChatViewState.MessagesBatchFetchSuccess -> previousState.copy(
                    itemProgress = false,
                    messagesList = concatMessagesList(previousState.messagesList,
                            partialState.messagesBatchList.map { convertToMessageViewState(it) })
            )
            is PartialChatViewState.ItemProgressState -> previousState.copy(
                    itemProgress = true
            )
            is PartialChatViewState.ErrorState -> previousState.copy(
                    error = true,
                    dismissToast = partialState.dismissToast
            )
        }
    }

    private fun concatMessagesList(oldMessages: List<MessageViewState>, newMessages: List<MessageViewState>)
            : List<MessageViewState> {
        val newMessagesIds = newMessages.map { it.messageId }
        return oldMessages.filter { !newMessagesIds.contains(it.messageId) } + newMessages
    }

    private fun convertToMessageViewState(messageToConvert: Message, isSent: Boolean = true): MessageViewState {
        return with(messageToConvert) {
            MessageViewState(message, messageId, timestamp,
                    senderId == chatInteractor.currentUserId, isSent)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
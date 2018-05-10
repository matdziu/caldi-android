package com.caldi.chat

import android.arch.lifecycle.ViewModel
import com.caldi.chat.list.MessageViewState
import com.caldi.chat.models.Message
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel(private val chatInteractor: ChatInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(ChatViewState(progress = true))

    fun bind(chatView: ChatView, chatId: String, receiverId: String) {
        val newMessagesListeningToggleObservable = chatView.emitNewMessagesListeningToggle()
                .flatMap {
                    if (it) chatInteractor.listenForNewMessages(chatId)
                    else chatInteractor.stopListeningForNewMessages(chatId)
                }

        val batchFetchTriggerObservable = chatView.emitBachFetchTrigger()
                .flatMap { chatInteractor.fetchChatMessagesBatch(chatId, it) }

        val sentMessageObservable = chatView.emitSentMessage()
                .filter { it.isNotBlank() }
                .flatMap { chatInteractor.sendMessage(it.trim(), chatId, receiverId) }

        val mergedObservable = Observable.merge(arrayListOf(
                newMessagesListeningToggleObservable,
                batchFetchTriggerObservable,
                sentMessageObservable))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { chatView.render(it) })
    }

    private fun reduce(previousState: ChatViewState, partialState: PartialChatViewState)
            : ChatViewState {
        return when (partialState) {
            is PartialChatViewState.MessagesListChanged -> ChatViewState(
                    messagesList = partialState.updatedMessagesList.map { convertToMessageViewState(it) }
            )
            is PartialChatViewState.NewMessagesListenerRemoved -> previousState
            is PartialChatViewState.ErrorState -> previousState.copy(
                    progress = false,
                    error = true,
                    dismissToast = partialState.dismissToast
            )
        }
    }

    private fun convertToMessageViewState(messageToConvert: Message): MessageViewState {
        return with(messageToConvert) {
            MessageViewState(message, messageId, timestamp,
                    senderId == chatInteractor.currentUserId, isSent)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
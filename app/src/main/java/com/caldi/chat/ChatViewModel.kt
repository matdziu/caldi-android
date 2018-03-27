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
            is PartialChatViewState.MessageSendingSuccess -> previousState
            is PartialChatViewState.NewMessageAdded -> previousState.copy(
                    newMessage = convertToMessageViewState(partialState.newMessage))
            is PartialChatViewState.NewMessagesListenerRemoved -> previousState
            is PartialChatViewState.MessagesBatchFetchSuccess -> previousState.copy(
                    itemProgress = false,
                    messagesBatchList = partialState.messagesBatchList.map { convertToMessageViewState(it) }
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

    private fun convertToMessageViewState(messageToConvert: Message): MessageViewState {
        return with(messageToConvert) { MessageViewState(message, messageId, senderId == chatInteractor.currentUserId) }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
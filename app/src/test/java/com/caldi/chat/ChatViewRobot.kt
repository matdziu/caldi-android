package com.caldi.chat

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ChatViewRobot(chatViewModel: ChatViewModel) : BaseViewRobot<ChatViewState>() {

    private val messageSubject = PublishSubject.create<String>()
    private val batchFetchTriggerSubject = PublishSubject.create<String>()
    private val newMessagesListeningToggleSubject = PublishSubject.create<Boolean>()
    private val markAsReadSubject = PublishSubject.create<Boolean>()
    private val receiverProfileFetchTrigger = PublishSubject.create<Boolean>()

    private val chatView = object : ChatView {

        override fun emitReceiverProfileFetchTrigger(): Observable<Boolean> = receiverProfileFetchTrigger

        override fun emitMarkAsRead(): Observable<Boolean> = markAsReadSubject

        override fun emitNewMessagesListeningToggle(): Observable<Boolean> = newMessagesListeningToggleSubject

        override fun emitBachFetchTrigger(): Observable<String> = batchFetchTriggerSubject

        override fun emitSentMessage(): Observable<String> = messageSubject

        override fun render(chatViewState: ChatViewState) {
            renderedStates.add(chatViewState)
        }
    }

    init {
        chatViewModel.bind(chatView, "testChatId", "testReceiverId", "testEventId")
    }

    fun toggleNewMessagesListening(toggle: Boolean) {
        newMessagesListeningToggleSubject.onNext(toggle)
    }

    fun fetchMessagesBatch(fromTimestamp: String) {
        batchFetchTriggerSubject.onNext(fromTimestamp)
    }

    fun sendMessage(message: String) {
        messageSubject.onNext(message)
    }

    fun markMessagesAsRead() {
        markAsReadSubject.onNext(true)
    }

    fun fetchReceiverProfile() {
        receiverProfileFetchTrigger.onNext(true)
    }
}
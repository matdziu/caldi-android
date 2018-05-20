package com.caldi.chat

import android.arch.lifecycle.ViewModel
import com.caldi.chat.list.MessageViewState
import com.caldi.chat.models.Message
import com.caldi.common.models.EventProfileData
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.meetpeople.list.AnswerViewState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel(private val chatInteractor: ChatInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(ChatViewState(progress = true))

    fun bind(chatView: ChatView,
             chatId: String,
             receiverId: String,
             eventId: String) {
        val newMessagesListeningToggleObservable = chatView.emitNewMessagesListeningToggle()
                .flatMap {
                    if (it) chatInteractor.listenForNewMessages(eventId, chatId)
                    else chatInteractor.stopListeningForNewMessages(eventId, chatId)
                }

        val receiverProfileFetchObservable =
                chatView.emitReceiverProfileFetchTrigger()
                        .flatMap {
                            ((Observable.zip(
                                    chatInteractor.fetchEventProfileData(eventId, receiverId),
                                    chatInteractor.fetchQuestions(eventId),
                                    BiFunction<EventProfileData, Map<String, String>, PersonProfileViewState>
                                    { eventProfileData, questions ->
                                        convertToPersonProfileViewState(eventProfileData, questions)
                                    })
                                    .map { PartialChatViewState.ReceiverProfileFetchedState(it) }))
                        }

        val markAsReadObservable = chatView.emitMarkAsRead()
                .flatMap { chatInteractor.setMessagesAsRead(eventId, chatId) }

        val batchFetchTriggerObservable = chatView.emitBachFetchTrigger()
                .flatMap { chatInteractor.fetchChatMessagesBatch(eventId, chatId, it) }

        val sentMessageObservable = chatView.emitSentMessage()
                .filter { it.isNotBlank() }
                .flatMap { chatInteractor.sendMessage(it.trim(), eventId, chatId, receiverId) }

        val mergedObservable = Observable.merge(arrayListOf(
                newMessagesListeningToggleObservable,
                receiverProfileFetchObservable,
                batchFetchTriggerObservable,
                markAsReadObservable,
                sentMessageObservable))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe { chatView.render(it) })
    }

    private fun reduce(previousState: ChatViewState, partialState: PartialChatViewState)
            : ChatViewState {
        return when (partialState) {
            is PartialChatViewState.MessagesListChanged -> previousState.copy(
                    progress = false,
                    messagesList = partialState.updatedMessagesList.map { convertToMessageViewState(it) }
            )
            is PartialChatViewState.NewMessagesListenerRemoved -> previousState
            is PartialChatViewState.ErrorState -> previousState.copy(
                    progress = false,
                    error = true,
                    dismissToast = partialState.dismissToast
            )
            is PartialChatViewState.MessagesSetAsRead -> previousState
            is PartialChatViewState.ReceiverProfileFetchedState -> previousState.copy(
                    progress = false,
                    receiverProfile = partialState.personProfileViewState
            )
        }
    }

    private fun convertToMessageViewState(messageToConvert: Message): MessageViewState {
        return with(messageToConvert) {
            MessageViewState(message, messageId, timestamp,
                    senderId == chatInteractor.currentUserId, isSent)
        }
    }

    private fun convertToPersonProfileViewState(eventProfileData: EventProfileData,
                                                questions: Map<String, String>)
            : PersonProfileViewState {
        return with(eventProfileData) {
            PersonProfileViewState(
                    userId,
                    eventUserName,
                    profilePicture,
                    userLinkUrl,
                    convertToAnswerViewStateList(questions, answers))
        }
    }

    private fun convertToAnswerViewStateList(questions: Map<String, String>, answers: Map<String, String>)
            : List<AnswerViewState> {
        val answerViewStateList = arrayListOf<AnswerViewState>()
        for ((questionId, question) in questions) {
            answerViewStateList.add(AnswerViewState(question, answers[questionId] ?: ""))
        }
        return answerViewStateList
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
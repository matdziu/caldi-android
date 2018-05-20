package com.caldi.chat

import com.caldi.chat.list.MessageViewState
import com.caldi.chat.models.Message
import com.caldi.common.models.EventProfileData
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.meetpeople.list.AnswerViewState
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class ChatViewModelTest {

    private val chatInteractor: ChatInteractor = mock()
    private val chatViewModel: ChatViewModel = ChatViewModel(chatInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testNewMessagesListenerSetting() {
        val updatedMessageList = listOf<Message>()
        whenever(chatInteractor.listenForNewMessages(any(), any())).thenReturn(
                Observable.just(PartialChatViewState.MessagesListChanged(updatedMessageList))
        )

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.toggleNewMessagesListening(true)

        chatViewRobot.assertViewStates(
                ChatViewState(progress = true),
                ChatViewState()
        )
    }

    @Test
    fun testSuccessfulMessagesBatchFetching() {
        val updatedMessageList = listOf(Message("2018-03-29", "Test message",
                "testSenderId", "testReceiverId", "testMessageId", true))
        whenever(chatInteractor.fetchChatMessagesBatch(any(), any(), any())).thenReturn(
                Observable.just(PartialChatViewState.MessagesListChanged(updatedMessageList))
        )
        whenever(chatInteractor.currentUserId).thenReturn("testSenderId")

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.fetchMessagesBatch("2018-03-29")

        chatViewRobot.assertViewStates(
                ChatViewState(progress = true),
                ChatViewState(messagesList = listOf(MessageViewState("Test message",
                        "testMessageId", "2018-03-29", true, true)))
        )
    }

    @Test
    fun testEmptyMessageSending() {
        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.sendMessage("")
        chatViewRobot.sendMessage("\n")

        chatViewRobot.assertViewStates(
                ChatViewState(progress = true)
        )
    }

    @Test
    fun testNotEmptyMessageSending() {
        val updatedMessageList = listOf(Message("2018-03-29", "this is test message",
                "testSenderId", "testReceiverId", "testMessageId", true))
        whenever(chatInteractor.sendMessage(eq("this is test message"), any(), any(), any())).thenReturn(
                Observable.just(PartialChatViewState.MessagesListChanged(updatedMessageList))
        )
        whenever(chatInteractor.currentUserId).thenReturn("testSenderId")

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.sendMessage("this is test message")

        chatViewRobot.assertViewStates(
                ChatViewState(progress = true),
                ChatViewState(messagesList = listOf(MessageViewState(
                        "this is test message", "testMessageId",
                        "2018-03-29", true, true))))
    }

    @Test
    fun testMarkingMessagesAsRead() {
        whenever(chatInteractor.setMessagesAsRead(any(), any())).thenReturn(Observable.just(PartialChatViewState.MessagesSetAsRead()))

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.markMessagesAsRead()

        chatViewRobot.assertViewStates(
                ChatViewState(progress = true),
                ChatViewState(progress = true)
        )
    }

    @Test
    fun testRemovalOfNewMessagesListener() {
        whenever(chatInteractor.stopListeningForNewMessages(any(), any())).thenReturn(
                Observable.just(PartialChatViewState.NewMessagesListenerRemoved())
        )

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.toggleNewMessagesListening(false)

        chatViewRobot.assertViewStates(
                ChatViewState(progress = true),
                ChatViewState(progress = true)
        )
    }

    @Test
    fun testReceiverProfileFetching() {
        val firstQuestion = "What is your favourite color?"
        val secondQuestion = "What is your favourite drink?"

        val firstAnswer = "Red"
        val secondAnswer = "Whisky sour"

        val userId = "11111"
        val eventUserName = "TestUsername"
        val profilePicture = "url/to/pic"
        val userLinkUrl = ""
        val answers = mapOf(
                "1a" to firstAnswer,
                "1b" to secondAnswer
        )

        val eventProfileData = EventProfileData(
                userId,
                eventUserName,
                answers,
                profilePicture,
                userLinkUrl
        )

        val questions = mapOf(
                "1a" to firstQuestion,
                "1b" to secondQuestion
        )

        whenever(chatInteractor.fetchEventProfileData(any(), any())).thenReturn(Observable.just(eventProfileData))
        whenever(chatInteractor.fetchQuestions(any())).thenReturn(Observable.just(questions))

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.fetchReceiverProfile()

        chatViewRobot.assertViewStates(
                ChatViewState(
                        progress = true),
                ChatViewState(
                        progress = false,
                        receiverProfile = PersonProfileViewState(
                                userId,
                                eventUserName,
                                profilePicture,
                                userLinkUrl,
                                listOf(
                                        AnswerViewState(firstQuestion, firstAnswer),
                                        AnswerViewState(secondQuestion, secondAnswer)
                                )
                        ))
        )
    }
}
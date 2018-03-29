package com.caldi.chat

import com.caldi.chat.list.MessageViewState
import com.caldi.chat.models.Message
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class ChatViewModelTest {

    private val chatInteractor: ChatInteractor = mock()
    private val chatViewModel: ChatViewModel = ChatViewModel(chatInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testNewMessagesListenerSetting() {
        val updatedMessageList = listOf<Message>()
        whenever(chatInteractor.listenForNewMessages(any())).thenReturn(
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
                "testSenderId", "testMessageId", true))
        whenever(chatInteractor.fetchChatMessagesBatch(any(), any())).thenReturn(
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
                "testSenderId", "testMessageId", true))
        whenever(chatInteractor.sendMessage(eq("this is test message"), any())).thenReturn(
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
    fun testRemovalOfNewMessagesListener() {
        whenever(chatInteractor.stopListeningForNewMessages(any())).thenReturn(
                Observable.just(PartialChatViewState.NewMessagesListenerRemoved())
        )

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.toggleNewMessagesListening(false)

        chatViewRobot.assertViewStates(
                ChatViewState(progress = true),
                ChatViewState(progress = true)
        )
    }
}
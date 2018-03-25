package com.caldi.chat

import com.caldi.chat.models.Message
import com.nhaarman.mockito_kotlin.any
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
    fun testSuccessfulMessageSending() {
        whenever(chatInteractor.sendMessage(any(), any())).
                thenReturn(Observable.just(PartialChatViewState.MessageSendingSuccess()))

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.sendMessage("This is test message")

        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState())
    }

    @Test
    fun testNewMessageAdditionListening() {
        whenever(chatInteractor.listenForNewMessages(any())).thenReturn(
                Observable.just(PartialChatViewState.NewMessageAdded(Message(messageId = "testABC")))
        )

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.toggleNewMessagesListening(true)

        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState(newMessage = Message(messageId = "testABC"))
        )
    }

    @Test
    fun testRemovalOfNewMessagesListener() {
        whenever(chatInteractor.stopListeningForNewMessages(any())).thenReturn(
                Observable.just(PartialChatViewState.NewMessagesListenerRemoved())
        )

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.toggleNewMessagesListening(false)

        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState()
        )
    }

    @Test
    fun testSuccessfulBatchFetch() {
        val messagesBatchList = listOf(Message(messageId = "firstMessageABC"), Message(messageId = "secondMessageABC"))
        whenever(chatInteractor.fetchChatMessagesBatch(any(), any())).thenReturn(
                Observable.just(PartialChatViewState.MessagesBatchFetchSuccess(messagesBatchList))
        )

        val chatViewRobot = ChatViewRobot(chatViewModel)

        chatViewRobot.fetchMessagesBatch("2018-03-25:23:01:11")

        chatViewRobot.assertViewStates(
                ChatViewState(),
                ChatViewState(itemProgress = true),
                ChatViewState(itemProgress = false, messagesBatchList = messagesBatchList)
        )
    }
}
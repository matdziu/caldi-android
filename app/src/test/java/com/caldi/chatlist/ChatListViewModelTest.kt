package com.caldi.chatlist

import com.caldi.chatlist.models.ChatItem
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import java.util.concurrent.TimeUnit

class ChatListViewModelTest {

    private val chatListInteractor: ChatListInteractor = mock()
    private val chatListViewModel: ChatListViewModel = ChatListViewModel(chatListInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testChatListFetchingSuccess() {
        whenever(chatListInteractor.fetchUserChatList(any())).thenReturn(Observable.just(
                PartialChatListViewState.SuccessfulChatListFetch(listOf(ChatItem("1", "Matt", "url/to/pic")))
        ))

        val chatListViewRobot = ChatListViewRobot(chatListViewModel)

        chatListViewRobot.triggerChatListFetching("testEventId")

        chatListViewRobot.assertViewStates(
                ChatListViewState(),
                ChatListViewState(progress = true),
                ChatListViewState(chatItemList = listOf(ChatItem("1", "Matt", "url/to/pic")))
        )
    }

    @Test
    fun testChatListFetchingError() {
        whenever(chatListInteractor.fetchUserChatList(any())).thenReturn(
                Observable.timer(100, TimeUnit.MILLISECONDS)
                        .map { PartialChatListViewState.ErrorState(true) }
                        .startWith(PartialChatListViewState.ErrorState())
                        as Observable<PartialChatListViewState>
        )

        val chatListViewRobot = ChatListViewRobot(chatListViewModel)

        chatListViewRobot.triggerChatListFetching("testEventId")

        chatListViewRobot.assertViewStates(
                ChatListViewState(),
                ChatListViewState(progress = true),
                ChatListViewState(error = true),
                ChatListViewState(error = true, dismissToast = true)
        )
    }
}
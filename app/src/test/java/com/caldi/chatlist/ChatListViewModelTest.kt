package com.caldi.chatlist

import com.caldi.chatlist.models.ChatItem
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class ChatListViewModelTest {

    private val chatListInteractor: ChatListInteractor = mock()
    private val chatListViewModel: ChatListViewModel = ChatListViewModel(chatListInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testChatsFetchingSuccess() {
        whenever(chatListInteractor.fetchChatList(any(), any())).thenReturn(Observable.just(
                PartialChatListViewState.SuccessfulChatListBatchFetch(listOf(ChatItem("1", "Matt", "url/to/pic")))
        ))

        val chatListViewRobot = ChatListViewRobot(chatListViewModel)

        chatListViewRobot.triggerChatsFetching()

        chatListViewRobot.assertViewStates(
                ChatListViewState(),
                ChatListViewState(progress = true),
                ChatListViewState(chatItemList = listOf(ChatItem("1", "Matt", "url/to/pic")))
        )
    }

    @Test
    fun testChatsFetchingError() {
        whenever(chatListInteractor.fetchChatList(any(), any())).thenReturn(
                Observable.just(PartialChatListViewState.ErrorState(true))
                        .startWith(PartialChatListViewState.ErrorState())
                        as Observable<PartialChatListViewState>
        )

        val chatListViewRobot = ChatListViewRobot(chatListViewModel)

        chatListViewRobot.triggerChatsFetching()

        chatListViewRobot.assertViewStates(
                ChatListViewState(),
                ChatListViewState(progress = true),
                ChatListViewState(error = true),
                ChatListViewState(error = true, dismissToast = true)
        )
    }
}
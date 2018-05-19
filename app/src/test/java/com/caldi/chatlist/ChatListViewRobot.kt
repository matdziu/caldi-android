package com.caldi.chatlist

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ChatListViewRobot(chatListViewModel: ChatListViewModel) : BaseViewRobot<ChatListViewState>() {

    private val userChatListFetchTriggerSubject = PublishSubject.create<String>()

    private val chatListView = object : ChatListView {

        override fun emitChatListFetchTrigger(): Observable<String> = userChatListFetchTriggerSubject

        override fun render(chatListViewState: ChatListViewState) {
            renderedStates.add(chatListViewState)
        }
    }

    init {
        chatListViewModel.bind(chatListView, "testEventId")
    }

    fun triggerChatListFetching(eventId: String) {
        userChatListFetchTriggerSubject.onNext(eventId)
    }
}
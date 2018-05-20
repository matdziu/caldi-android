package com.caldi.chatlist

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ChatListViewRobot(chatListViewModel: ChatListViewModel) : BaseViewRobot<ChatListViewState>() {

    private val chatsFetchTrigger = PublishSubject.create<Boolean>()
    private val chatItemChangeListenerToggle = PublishSubject.create<Boolean>()

    private val chatListView = object : ChatListView {

        override fun emitChatItemChangeListenerToggle(): Observable<Boolean> = chatItemChangeListenerToggle

        override fun emitChatsFetchTrigger(): Observable<Boolean> = chatsFetchTrigger

        override fun render(chatListViewState: ChatListViewState) {
            renderedStates.add(chatListViewState)
        }
    }

    init {
        chatListViewModel.bind(chatListView, "testEventId")
    }

    fun triggerChatsFetching() {
        chatsFetchTrigger.onNext(true)
    }

    fun toggleChatItemChangeListener(toggle: Boolean) {
        chatItemChangeListenerToggle.onNext(toggle)
    }
}
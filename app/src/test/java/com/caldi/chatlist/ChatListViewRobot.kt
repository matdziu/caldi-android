package com.caldi.chatlist

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class ChatListViewRobot(chatListViewModel: ChatListViewModel) : BaseViewRobot<ChatListViewState>() {

    private val unreadChatsFetchTriggerSubject = PublishSubject.create<Boolean>()
    private val readChatsFetchTriggerSubject = PublishSubject.create<Boolean>()
    private val chatItemChangeListenerToggle = PublishSubject.create<Boolean>()

    private val chatListView = object : ChatListView {

        override fun emitChatItemChangeListenerToggle(): Observable<Boolean> = chatItemChangeListenerToggle

        override fun emitUnreadChatsFetchTrigger(): Observable<Boolean> = unreadChatsFetchTriggerSubject

        override fun emitReadChatsFetchTrigger(): Observable<Boolean> = readChatsFetchTriggerSubject

        override fun render(chatListViewState: ChatListViewState) {
            renderedStates.add(chatListViewState)
        }
    }

    init {
        chatListViewModel.bind(chatListView, "testEventId")
    }

    fun triggerReadChatsFetching() {
        readChatsFetchTriggerSubject.onNext(true)
    }

    fun triggerUnreadChatsFetching() {
        unreadChatsFetchTriggerSubject.onNext(true)
    }

    fun toggleChatItemChangeListener(toggle: Boolean) {
        chatItemChangeListenerToggle.onNext(toggle)
    }
}
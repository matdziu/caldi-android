package com.caldi.chatlist

import io.reactivex.Observable

interface ChatListView {

    fun emitChatsFetchTrigger(): Observable<Boolean>

    fun emitChatItemChangeListenerToggle(): Observable<Boolean>

    fun render(chatListViewState: ChatListViewState)
}
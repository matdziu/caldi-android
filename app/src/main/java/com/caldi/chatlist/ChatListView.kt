package com.caldi.chatlist

import io.reactivex.Observable

interface ChatListView {

    fun emitReadChatsFetchTrigger(): Observable<Boolean>

    fun emitUnreadChatsFetchTrigger(): Observable<Boolean>

    fun emitChatItemChangeListenerToggle(): Observable<Boolean>

    fun render(chatListViewState: ChatListViewState)
}
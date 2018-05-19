package com.caldi.chatlist

import io.reactivex.Observable

interface ChatListView {

    fun emitReadChatsFetchTrigger(): Observable<String>

    fun emitUnreadChatsFetchTrigger(): Observable<Boolean>

    fun render(chatListViewState: ChatListViewState)
}
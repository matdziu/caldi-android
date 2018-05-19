package com.caldi.chatlist

import io.reactivex.Observable

interface ChatListView {

    fun emitChatListFetchTrigger(): Observable<String>

    fun render(chatListViewState: ChatListViewState)
}
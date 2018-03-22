package com.caldi.chatlist

import io.reactivex.Observable

interface ChatListView {

    fun emitUserChatListFetchTrigger(): Observable<String>

    fun render(chatListViewState: ChatListViewState)
}
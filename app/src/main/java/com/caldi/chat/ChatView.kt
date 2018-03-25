package com.caldi.chat

import io.reactivex.Observable

interface ChatView {

    fun emitNewMessagesListeningToggle(): Observable<Boolean>

    fun emitBachFetchTrigger(): Observable<String>

    fun emitSentMessage(): Observable<String>

    fun render(chatViewState: ChatViewState)
}
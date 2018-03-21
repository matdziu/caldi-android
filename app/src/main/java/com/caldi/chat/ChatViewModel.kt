package com.caldi.chat

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatViewModel(private val chatInteractor: ChatInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialChatViewState>()

    fun bind(chatView: ChatView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
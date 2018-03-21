package com.caldi.chatlist

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatListViewModel(private val chatListInteractor: ChatListInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialChatListViewState>()

    fun bind(chatListView: ChatListView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
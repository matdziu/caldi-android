package com.caldi.chatlist

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatListViewModel(private val chatListInteractor: ChatListInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(ChatListViewState())

    fun bind(chatListView: ChatListView, eventId: String) {
        val unreadChatsFetchObservable = chatListView.emitUnreadChatsFetchTrigger()
                .flatMap {
                    chatListInteractor.fetchUnreadChatsList(eventId)
                            .startWith(PartialChatListViewState.ProgressState())
                }

        val readChatsFetchObservable = chatListView.emitReadChatsFetchTrigger()
                .flatMap {
                    chatListInteractor
                            .fetchReadChatsList(eventId, it)
                            .startWith(PartialChatListViewState.ProgressState())
                }

        val mergedObservable = Observable.merge(listOf(
                unreadChatsFetchObservable,
                readChatsFetchObservable))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { chatListView.render(it) })
    }

    private fun reduce(previousState: ChatListViewState, partialState: PartialChatListViewState)
            : ChatListViewState {
        return when (partialState) {
            is PartialChatListViewState.ProgressState -> ChatListViewState(progress = true)
            is PartialChatListViewState.ErrorState -> ChatListViewState(error = true, dismissToast = partialState.dismissToast)
            is PartialChatListViewState.SuccessfulChatListFetch -> ChatListViewState(chatItemList = partialState.chatList)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
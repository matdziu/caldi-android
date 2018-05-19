package com.caldi.chatlist

import android.arch.lifecycle.ViewModel
import com.caldi.chatlist.models.ChatItem
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class ChatListViewModel(private val chatListInteractor: ChatListInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(ChatListViewState())
    private var currentChatItemList = listOf<ChatItem>()

    fun bind(chatListView: ChatListView, eventId: String) {
        val unreadChatsFetchObservable = chatListView.emitUnreadChatsFetchTrigger()
                .flatMap {
                    chatListInteractor.fetchUnreadChatsList(eventId)
                            .startWith(PartialChatListViewState.ProgressState())
                }

        val readChatsFetchObservable = chatListView.emitReadChatsFetchTrigger()
                .flatMap {
                    val fromChatId = if (currentChatItemList.isNotEmpty() &&
                            !currentChatItemList.last().unread) currentChatItemList.last().chatId else ""
                    chatListInteractor
                            .fetchReadChatsList(eventId, fromChatId)
                            .startWith(PartialChatListViewState.ProgressState())
                }

        val chatItemChangeListenerObservable = chatListView.emitChatItemChangeListenerToggle()
                .flatMap {
                    if (it) chatListInteractor.listenForChatItemChange(eventId)
                    else chatListInteractor.stopListeningForChatItemChange(eventId)
                }

        val mergedObservable = Observable.merge(listOf(
                unreadChatsFetchObservable,
                readChatsFetchObservable,
                chatItemChangeListenerObservable))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { chatListView.render(it) })
    }

    private fun reduce(previousState: ChatListViewState, partialState: PartialChatListViewState)
            : ChatListViewState {
        return when (partialState) {
            is PartialChatListViewState.ProgressState -> ChatListViewState(
                    progress = true)
            is PartialChatListViewState.ErrorState -> ChatListViewState(
                    error = true,
                    dismissToast = partialState.dismissToast)
            is PartialChatListViewState.SuccessfulChatListBatchFetch -> ChatListViewState(
                    chatItemList = addBatchToChatItemList(partialState.chatList))
            is PartialChatListViewState.ChatItemChanged -> previousState.copy(
                    chatItemList = updateChatItemOnList(partialState.chatItem))
            is PartialChatListViewState.ChatItemListenerRemoved -> previousState
        }
    }

    private fun addBatchToChatItemList(chatItemBatch: List<ChatItem>): List<ChatItem> {
        val newList = currentChatItemList + chatItemBatch
        currentChatItemList = newList.distinctBy { it.chatId }
        return currentChatItemList
    }

    private fun updateChatItemOnList(chatItem: ChatItem): List<ChatItem> {
        val newList = ArrayList(currentChatItemList)
        val indexToRemove = newList.indexOfFirst { it.chatId == chatItem.chatId }
        newList.removeAt(indexToRemove)
        newList.add(0, chatItem)
        currentChatItemList = newList
        return currentChatItemList
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
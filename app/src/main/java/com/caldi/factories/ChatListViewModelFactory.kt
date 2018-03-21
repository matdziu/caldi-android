package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.chatlist.ChatListInteractor
import com.caldi.chatlist.ChatListViewModel

@Suppress("UNCHECKED_CAST")
class ChatListViewModelFactory(private val chatListInteractor: ChatListInteractor)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatListViewModel(chatListInteractor) as T
    }
}
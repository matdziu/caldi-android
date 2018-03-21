package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.chat.ChatInteractor
import com.caldi.chat.ChatViewModel

@Suppress("UNCHECKED_CAST")
class ChatViewModelFactory(private val chatInteractor: ChatInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(chatInteractor) as T
    }
}
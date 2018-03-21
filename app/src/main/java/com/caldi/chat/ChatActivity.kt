package com.caldi.chat

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.ChatViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class ChatActivity : BaseDrawerActivity(), ChatView {

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory

    private lateinit var chatViewModel: ChatViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_chat)
        super.onCreate(savedInstanceState)

        chatViewModel = ViewModelProviders.of(this, chatViewModelFactory)[ChatViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.chat_item)
        chatViewModel.bind(this)
    }

    override fun onStop() {
        chatViewModel.unbind()
        super.onStop()
    }

    override fun render(chatViewState: ChatViewState) {

    }
}
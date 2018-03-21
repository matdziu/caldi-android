package com.caldi.chatlist

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.ChatListViewModelFactory
import dagger.android.AndroidInjection
import javax.inject.Inject

class ChatListActivity : BaseDrawerActivity(), ChatListView {

    @Inject
    lateinit var chatListViewModelFactory: ChatListViewModelFactory

    private lateinit var chatListViewModel: ChatListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_chat_list)
        super.onCreate(savedInstanceState)

        chatListViewModel = ViewModelProviders.of(this, chatListViewModelFactory)[ChatListViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.chat_item)
        chatListViewModel.bind(this)
    }

    override fun onStop() {
        chatListViewModel.unbind()
        super.onStop()
    }

    override fun render(chatListViewState: ChatListViewState) {

    }
}
package com.caldi.chatlist

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.factories.ChatListViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ChatListActivity : BaseDrawerActivity(), ChatListView {

    @Inject
    lateinit var chatListViewModelFactory: ChatListViewModelFactory

    private lateinit var chatListViewModel: ChatListViewModel

    private val userChatListFetchTriggerSubject = PublishSubject.create<String>()

    private var fetchChatListOnStart = true

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
        if (fetchChatListOnStart) userChatListFetchTriggerSubject.onNext(eventId)
    }

    override fun onStop() {
        fetchChatListOnStart = false
        chatListViewModel.unbind()
        super.onStop()
    }

    override fun emitUserChatListFetchTrigger(): Observable<String> = userChatListFetchTriggerSubject

    override fun render(chatListViewState: ChatListViewState) {
        Log.d("mateusz", chatListViewState.toString())
    }
}
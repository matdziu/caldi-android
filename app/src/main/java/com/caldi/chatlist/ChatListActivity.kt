package com.caldi.chatlist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.chatlist.list.ChatItemsAdapter
import com.caldi.chatlist.models.ChatItem
import com.caldi.factories.ChatListViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_chat_list.chatItemsRecyclerView
import kotlinx.android.synthetic.main.activity_chat_list.progressBar
import javax.inject.Inject

class ChatListActivity : BaseDrawerActivity(), ChatListView {

    @Inject
    lateinit var chatListViewModelFactory: ChatListViewModelFactory

    private lateinit var chatListViewModel: ChatListViewModel

    private val userChatListFetchTriggerSubject = PublishSubject.create<String>()

    private val chatItemsAdapter = ChatItemsAdapter()

    private var fetchChatListOnStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_chat_list)
        super.onCreate(savedInstanceState)

        chatListViewModel = ViewModelProviders.of(this, chatListViewModelFactory)[ChatListViewModel::class.java]

        chatItemsRecyclerView.adapter = chatItemsAdapter
        chatItemsRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        fetchChatListOnStart = true
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.chat_item)
    }

    override fun onStart() {
        super.onStart()
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
        with(chatListViewState) {
            showProgress(progress)
            showError(error, dismissToast)
            setChatItemList(progress, error, chatItemList)
        }
    }

    private fun showProgress(progress: Boolean) {
        if (progress) {
            progressBar.visibility = View.VISIBLE
            chatItemsRecyclerView.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            chatItemsRecyclerView.visibility = View.VISIBLE
        }
    }

    private fun showError(error: Boolean, dismissToast: Boolean) {
        if (error && !dismissToast) {
            Toast.makeText(this, getString(R.string.event_list_fetching_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun setChatItemList(progress: Boolean, error: Boolean, chatItemList: List<ChatItem>) {
        if (!progress && !error) {
            chatItemsAdapter.setChatItemList(chatItemList)
        }
    }
}
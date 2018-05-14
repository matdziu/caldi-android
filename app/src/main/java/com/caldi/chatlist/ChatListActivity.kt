package com.caldi.chatlist

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.chatlist.list.ChatItemsAdapter
import com.caldi.chatlist.models.ChatItem
import com.caldi.constants.EVENT_ID_KEY
import com.caldi.factories.ChatListViewModelFactory
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_chat_list.chatItemsRecyclerView
import kotlinx.android.synthetic.main.activity_chat_list.noPeopleToChatTextView
import kotlinx.android.synthetic.main.activity_chat_list.progressBar
import javax.inject.Inject

class ChatListActivity : BaseDrawerActivity(), ChatListView {

    @Inject
    lateinit var chatListViewModelFactory: ChatListViewModelFactory

    private lateinit var chatListViewModel: ChatListViewModel

    private lateinit var userChatListFetchTriggerSubject: Subject<String>

    private lateinit var chatItemsAdapter: ChatItemsAdapter

    private var fetchChatListOnStart = true

    companion object {

        fun start(context: Context, eventId: String) {
            val intent = Intent(context, ChatListActivity::class.java)
            intent.putExtra(EVENT_ID_KEY, eventId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_chat_list)
        super.onCreate(savedInstanceState)

        chatItemsAdapter = ChatItemsAdapter(eventId)

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
        initEmitters()
        chatListViewModel.bind(this)
        if (fetchChatListOnStart) userChatListFetchTriggerSubject.onNext(eventId)
    }

    private fun initEmitters() {
        userChatListFetchTriggerSubject = PublishSubject.create()
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
        noPeopleToChatTextView.visibility = View.GONE
        if (!progress && !error && chatItemList.isNotEmpty()) {
            chatItemsAdapter.setChatItemList(chatItemList)
        } else if (!progress && !error && chatItemList.isEmpty()) {
            noPeopleToChatTextView.visibility = View.VISIBLE
        }
    }
}
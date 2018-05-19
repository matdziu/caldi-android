package com.caldi.chatlist

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
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

    private lateinit var readChatsTriggerSubject: Subject<String>

    private lateinit var unreadChatsTriggerSubject: Subject<Boolean>

    private lateinit var chatItemsAdapter: ChatItemsAdapter

    private var initialFetch = true

    private var isBatchLoading = false

    private var recentChatItemsBatch = listOf<ChatItem>()

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

        initRecyclerView()
    }

    private fun initRecyclerView() {
        chatItemsRecyclerView.adapter = chatItemsAdapter
        chatItemsRecyclerView.layoutManager = LinearLayoutManager(this)
        chatItemsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(1) && !isBatchLoading) {
                    isBatchLoading = true
                    readChatsTriggerSubject.onNext(
                            if (recentChatItemsBatch.isNotEmpty() &&
                                    !recentChatItemsBatch.last().unread) recentChatItemsBatch.last().chatId else ""
                    )
                }
            }
        })
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initialFetch = true
    }

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.chat_item)
    }

    override fun onStart() {
        super.onStart()
        initEmitters()
        chatListViewModel.bind(this, eventId)
        if (initialFetch) {
            unreadChatsTriggerSubject.onNext(true)
        }
    }

    private fun initEmitters() {
        readChatsTriggerSubject = PublishSubject.create()
        unreadChatsTriggerSubject = PublishSubject.create()
    }

    override fun onStop() {
        initialFetch = false
        chatListViewModel.unbind()
        super.onStop()
    }

    override fun emitReadChatsFetchTrigger(): Observable<String> = readChatsTriggerSubject

    override fun emitUnreadChatsFetchTrigger(): Observable<Boolean> = unreadChatsTriggerSubject

    override fun render(chatListViewState: ChatListViewState) {
        with(chatListViewState) {
            showProgress(progress)
            showError(error, dismissToast)
            addChatItemBatch(chatItemList)
            showNoChatsHint(chatItemsAdapter.currentChatItemList, progress, error)
        }
    }

    private fun showProgress(progress: Boolean) {
        if (progress) {
            progressBar.visibility = View.VISIBLE
        } else {
            progressBar.visibility = View.GONE
        }
    }

    private fun showError(error: Boolean, dismissToast: Boolean) {
        if (error && !dismissToast) {
            Toast.makeText(this, getString(R.string.event_list_fetching_error), Toast.LENGTH_SHORT).show()
        }
    }

    private fun addChatItemBatch(chatItemList: List<ChatItem>) {
        noPeopleToChatTextView.visibility = View.GONE
        if (chatItemList.isNotEmpty() && recentChatItemsBatch != chatItemList) {
            recentChatItemsBatch = chatItemList
            chatItemsAdapter.addChatItemsBatch(chatItemList)
            isBatchLoading = false
        }
    }

    private fun showNoChatsHint(currentChatItemList: List<ChatItem>,
                                progress: Boolean, error: Boolean) {
        if (currentChatItemList.isEmpty() && !progress && !error) {
            noPeopleToChatTextView.visibility = View.VISIBLE
        } else {
            noPeopleToChatTextView.visibility = View.GONE
        }
    }
}
package com.caldi.chat

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.chat.list.MessagesAdapter
import com.caldi.chat.utils.MessagesAdapterObserver
import com.caldi.constants.CHAT_ID_KEY
import com.caldi.constants.CHAT_IMAGE_URL_KEY
import com.caldi.constants.CHAT_NAME_KEY
import com.caldi.extensions.getCurrentISODate
import com.caldi.factories.ChatViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_chat.chatInfoImageView
import kotlinx.android.synthetic.main.activity_chat.chatInfoTextView
import kotlinx.android.synthetic.main.activity_chat.chatInfoView
import kotlinx.android.synthetic.main.activity_chat.messageInputEditText
import kotlinx.android.synthetic.main.activity_chat.messagesRecyclerView
import kotlinx.android.synthetic.main.activity_chat.progressBar
import kotlinx.android.synthetic.main.activity_chat.sendMessageButton
import kotlinx.android.synthetic.main.activity_chat.sendPanelView
import javax.inject.Inject

class ChatActivity : BaseDrawerActivity(), ChatView {

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory

    private lateinit var chatViewModel: ChatViewModel

    private val newMessagesListeningToggleSubject = PublishSubject.create<Boolean>()
    private val batchFetchTriggerSubject = PublishSubject.create<String>()

    private var chatId = ""

    private var isNewMessagesListenerSet = false
    private var fetchInitialBatch = true
    private var isBatchLoading = false

    private val messagesAdapter = MessagesAdapter()

    private val messagesAdapterObserver: MessagesAdapterObserver by lazy {
        MessagesAdapterObserver { lastItemPosition ->
            if (lastItemPosition == 0) {
                isBatchLoading = false
            } else {
                messagesRecyclerView.scrollToPosition(lastItemPosition)
            }
        }
    }

    companion object {

        fun start(context: Context,
                  chatId: String,
                  name: String,
                  imageUrl: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CHAT_ID_KEY, chatId)
            intent.putExtra(CHAT_NAME_KEY, name)
            intent.putExtra(CHAT_IMAGE_URL_KEY, imageUrl)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_chat)
        super.onCreate(savedInstanceState)

        chatId = intent.getStringExtra(CHAT_ID_KEY)

        val chatName = intent.getStringExtra(CHAT_NAME_KEY)
        val imageUrl = intent.getStringExtra(CHAT_IMAGE_URL_KEY)

        chatInfoTextView.text = chatName
        Picasso.get()
                .load(imageUrl)
                .placeholder(R.drawable.profile_picture_shape)
                .into(chatInfoImageView)

        chatViewModel = ViewModelProviders.of(this, chatViewModelFactory)[ChatViewModel::class.java]

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        messagesRecyclerView.layoutManager = layoutManager
        messagesRecyclerView.adapter = messagesAdapter

        messagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (!recyclerView.canScrollVertically(-1) && !isBatchLoading) {
                    isBatchLoading = true
                    batchFetchTriggerSubject.onNext(messagesAdapter.getLastTimestamp())
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.chat_item)
        chatViewModel.bind(this, chatId)

        if (!isNewMessagesListenerSet) {
            newMessagesListeningToggleSubject.onNext(true)
            isNewMessagesListenerSet = true
        }

        if (fetchInitialBatch) {
            batchFetchTriggerSubject.onNext(getCurrentISODate())
            fetchInitialBatch = false
        }

        messagesAdapter.registerAdapterDataObserver(messagesAdapterObserver)
    }

    override fun onStop() {
        chatViewModel.unbind()
        messagesAdapter.unregisterAdapterDataObserver(messagesAdapterObserver)
        super.onStop()
    }

    override fun onDestroy() {
        newMessagesListeningToggleSubject.onNext(false)
        super.onDestroy()
    }

    override fun emitNewMessagesListeningToggle(): Observable<Boolean> = newMessagesListeningToggleSubject

    override fun emitBachFetchTrigger(): Observable<String> = batchFetchTriggerSubject

    override fun emitSentMessage(): Observable<String> {
        return RxView.clicks(sendMessageButton)
                .map { messageInputEditText.text.toString() }
                .doOnNext { messageInputEditText.setText("") }
    }

    override fun render(chatViewState: ChatViewState) {
        with(chatViewState) {
            showProgressBar(progress)
            messagesAdapter.submitList(messagesList)
        }
    }

    private fun showProgressBar(show: Boolean) {
        progressBar.visibility = View.GONE
        if (show) {
            progressBar.visibility = View.VISIBLE
            sendPanelView.visibility = View.GONE
            messagesRecyclerView.visibility = View.GONE
            chatInfoView.visibility = View.GONE
        } else {
            sendPanelView.visibility = View.VISIBLE
            messagesRecyclerView.visibility = View.VISIBLE
            chatInfoView.visibility = View.VISIBLE
        }
    }
}
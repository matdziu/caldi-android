package com.caldi.chat

import android.app.NotificationManager
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
import com.caldi.chatlist.models.ChatItem
import com.caldi.common.utils.MessagesAdapterObserver
import com.caldi.constants.CHAT_INFO_KEY
import com.caldi.constants.CHAT_MESSAGE_NOTIFICATION_REQUEST_CODE
import com.caldi.constants.NEW_CONNECTION_NOTIFICATION_REQUEST_CODE
import com.caldi.extensions.getCurrentISODate
import com.caldi.factories.ChatViewModelFactory
import com.caldi.injection.modules.GlideApp
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
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

    private lateinit var newMessagesListeningToggleSubject: Subject<Boolean>
    private lateinit var batchFetchTriggerSubject: Subject<String>

    var chatInfo: ChatItem = ChatItem()

    private var init = true
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
                  chatInfo: ChatItem) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CHAT_INFO_KEY, chatInfo)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_chat)
        super.onCreate(savedInstanceState)

        chatInfo = intent.getParcelableExtra(CHAT_INFO_KEY)

        chatInfoTextView.text = chatInfo.name
        if (chatInfo.imageUrl.isNotEmpty()) {
            GlideApp.with(this)
                    .load(chatInfo.imageUrl)
                    .placeholder(R.drawable.profile_picture_shape)
                    .into(chatInfoImageView)
        }

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

    override fun onResume() {
        super.onResume()
        setNavigationSelection(R.id.chat_item)
    }

    override fun onStart() {
        super.onStart()
        val notificationsManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationsManager.cancel(CHAT_MESSAGE_NOTIFICATION_REQUEST_CODE + chatInfo.chatId.hashCode())
        notificationsManager.cancel(NEW_CONNECTION_NOTIFICATION_REQUEST_CODE + chatInfo.chatId.hashCode())
        initEmitters()
        chatViewModel.bind(this, chatInfo.chatId, chatInfo.receiverId, eventId)
        if (init) {
            newMessagesListeningToggleSubject.onNext(true)
            batchFetchTriggerSubject.onNext(getCurrentISODate())
            init = false
        }

        messagesAdapter.registerAdapterDataObserver(messagesAdapterObserver)
    }

    private fun initEmitters() {
        newMessagesListeningToggleSubject = PublishSubject.create()
        batchFetchTriggerSubject = PublishSubject.create()
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
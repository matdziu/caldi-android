package com.caldi.chat

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.caldi.R
import com.caldi.base.BaseDrawerActivity
import com.caldi.constants.CHAT_ID_KEY
import com.caldi.factories.ChatViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_chat.messageInputEditText
import kotlinx.android.synthetic.main.activity_chat.sendMessageButton
import javax.inject.Inject

class ChatActivity : BaseDrawerActivity(), ChatView {

    @Inject
    lateinit var chatViewModelFactory: ChatViewModelFactory

    private lateinit var chatViewModel: ChatViewModel

    private val newMessagesListeningToggleSubject = PublishSubject.create<Boolean>()
    private val batchFetchTriggerSubject = PublishSubject.create<String>()

    private var chatId = ""

    private var isNewMessagesListenerSet = false

    companion object {

        fun start(context: Context, chatId: String) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra(CHAT_ID_KEY, chatId)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        setContentView(R.layout.activity_chat)
        super.onCreate(savedInstanceState)

        chatId = intent.getStringExtra(CHAT_ID_KEY)

        chatViewModel = ViewModelProviders.of(this, chatViewModelFactory)[ChatViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        setNavigationSelection(R.id.chat_item)
        chatViewModel.bind(this, chatId)
        if (!isNewMessagesListenerSet) {
            newMessagesListeningToggleSubject.onNext(true)
            isNewMessagesListenerSet = true
        }
    }

    override fun onStop() {
        chatViewModel.unbind()
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

    }
}
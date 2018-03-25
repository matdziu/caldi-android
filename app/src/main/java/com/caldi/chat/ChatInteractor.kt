package com.caldi.chat

import com.caldi.chat.models.Message
import com.caldi.chat.utils.NewMessageAddedListener
import com.caldi.constants.CHATS_NODE
import com.caldi.constants.TIMESTAMP_CHILD
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*
import java.util.concurrent.TimeUnit

class ChatInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var newMessageAddedListener: NewMessageAddedListener? = null

    private var batchSize = 10

    fun sendMessage(message: String, chatId: String): Observable<PartialChatViewState> {
        val stateSubject = PublishSubject.create<PartialChatViewState>()
        val messageNodeRef = getChatNodeReference(chatId).push()

        val messageId = UUID.randomUUID().toString()

        messageNodeRef.setValue(Message(
                message = message,
                senderId = currentUserId,
                messageId = messageId))
                .addOnSuccessListener { stateSubject.onNext(PartialChatViewState.MessageSendingSuccess(messageId)) }

        return stateSubject
    }

    fun fetchChatMessagesBatch(chatId: String, fromTimestamp: String): Observable<PartialChatViewState> {
        val stateSubject = PublishSubject.create<PartialChatViewState>()
        getChatNodeReference(chatId)
                .orderByChild(TIMESTAMP_CHILD)
                .endAt(fromTimestamp)
                .limitToLast(batchSize)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val messagesBatchList = arrayListOf<Message>()
                        for (messageSnapshot in dataSnapshot.children.toList()) {
                            messageSnapshot.getValue(Message::class.java)?.let { messagesBatchList.add(it) }
                        }
                        stateSubject.onNext(PartialChatViewState.MessagesBatchFetchSuccess(messagesBatchList))
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun listenForNewMessages(chatId: String): Observable<PartialChatViewState> {
        val stateSubject = PublishSubject.create<PartialChatViewState>()

        newMessageAddedListener = object : NewMessageAddedListener() {
            override fun onNewMessageAdded(newMessage: Message) {
                stateSubject.onNext(PartialChatViewState.NewMessageAdded(newMessage))
            }
        }

        getChatNodeReference(chatId).addChildEventListener(newMessageAddedListener)

        return stateSubject
    }

    fun stopListeningForNewMessages(chatId: String) {
        newMessageAddedListener?.let { getChatNodeReference(chatId).removeEventListener(it) }
    }

    private fun getChatNodeReference(chatId: String): DatabaseReference {
        return firebaseDatabase.getReference("$CHATS_NODE/$chatId")
    }

    private fun emitError(stateSubject: Subject<PartialChatViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialChatViewState.ErrorState(true) }
                .startWith(PartialChatViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
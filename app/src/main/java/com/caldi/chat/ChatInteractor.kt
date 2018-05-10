package com.caldi.chat

import com.caldi.chat.models.Message
import com.caldi.common.utils.NewMessageAddedListener
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

class ChatInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var newMessageAddedListener: NewMessageAddedListener<Message>? = null

    private var batchSize = 10

    private var currentMessagesList = listOf<Message>()

    fun sendMessage(message: String, chatId: String, receiverId: String): Observable<PartialChatViewState> {
        val messageNodeRef = getChatNodeReference(chatId).push()
        val messageObject = Message(
                message = message,
                senderId = currentUserId,
                receiverId = receiverId,
                messageId = messageNodeRef.key)

        messageNodeRef.setValue(messageObject)

        messageObject.isSent = false
        currentMessagesList += messageObject
        return Observable.just(PartialChatViewState.MessagesListChanged(currentMessagesList))
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
                        currentMessagesList = (messagesBatchList + currentMessagesList).distinctBy { it.messageId }
                        stateSubject.onNext(PartialChatViewState.MessagesListChanged(currentMessagesList))
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun listenForNewMessages(chatId: String): Observable<PartialChatViewState> {
        val stateSubject = PublishSubject.create<PartialChatViewState>()

        newMessageAddedListener = object : NewMessageAddedListener<Message>(Message::class.java) {
            override fun onNewMessageAdded(newMessage: Message) {
                currentMessagesList = currentMessagesList.filter { it.messageId != newMessage.messageId } + newMessage
                stateSubject.onNext(PartialChatViewState.MessagesListChanged(currentMessagesList))
            }
        }

        getChatNodeReference(chatId).addChildEventListener(newMessageAddedListener)

        return stateSubject
    }

    fun stopListeningForNewMessages(chatId: String): Observable<PartialChatViewState> {
        newMessageAddedListener?.let { getChatNodeReference(chatId).removeEventListener(it) }
        return Observable.just(PartialChatViewState.NewMessagesListenerRemoved())
    }

    private fun getChatNodeReference(chatId: String): DatabaseReference {
        return firebaseDatabase.getReference("$CHATS_NODE/$chatId")
    }

    private fun emitError(stateSubject: Subject<PartialChatViewState>) {
        stateSubject.onNext(PartialChatViewState.ErrorState())
        stateSubject.onNext(PartialChatViewState.ErrorState(true))
    }
}
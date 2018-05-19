package com.caldi.chatlist

import com.caldi.chatlist.models.ChatItem
import com.caldi.chatlist.utils.ChatItemChangedListener
import com.caldi.constants.UNREAD_CHILD
import com.caldi.constants.USERS_NODE
import com.caldi.constants.USER_CHATS_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class ChatListInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    private var chatItemChangedListener: ChatItemChangedListener? = null

    private val chatItemsBatchSize = 5

    fun fetchUnreadChatsList(eventId: String): Observable<PartialChatListViewState> {
        val stateSubject = PublishSubject.create<PartialChatListViewState>()
        getUserChatsNodeReference(eventId)
                .orderByChild(UNREAD_CHILD)
                .equalTo(true)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val unreadChatItems = dataSnapshot.children.map { it.getValue(ChatItem::class.java) as ChatItem }
                        if (unreadChatItems.isNotEmpty()) {
                            stateSubject.onNext(PartialChatListViewState.SuccessfulChatListBatchFetch(unreadChatItems))
                        } else {
                            fetchReadChatsList(eventId, "", stateSubject)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun fetchReadChatsList(eventId: String,
                           fromChatId: String,
                           stateSubject: Subject<PartialChatListViewState> = PublishSubject.create())
            : Observable<PartialChatListViewState> {
        getUserChatsNodeReference(eventId)
                .orderByKey()
                .startAt(fromChatId)
                .limitToFirst(chatItemsBatchSize)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        stateSubject.onNext(PartialChatListViewState.SuccessfulChatListBatchFetch(
                                dataSnapshot.children
                                        .map { it.getValue(ChatItem::class.java) as ChatItem }
                                        .filter { it.chatId != fromChatId && !it.unread }))
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun listenForChatItemChange(eventId: String): Observable<PartialChatListViewState> {
        val stateSubject = PublishSubject.create<PartialChatListViewState>()
        chatItemChangedListener = ChatItemChangedListener {
            stateSubject.onNext(
                    PartialChatListViewState.ChatItemChanged(it)
            )
        }
        getUserChatsNodeReference(eventId).addChildEventListener(chatItemChangedListener)
        return stateSubject
    }

    fun stopListeningForChatItemChange(eventId: String): Observable<PartialChatListViewState> {
        chatItemChangedListener?.let { getUserChatsNodeReference(eventId).removeEventListener(it) }
        return Observable.just(PartialChatListViewState.ChatItemListenerRemoved())
    }

    private fun getUserChatsNodeReference(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$currentUserId/$USER_CHATS_NODE/$eventId")
    }

    private fun emitError(stateSubject: Subject<PartialChatListViewState>) {
        stateSubject.onNext(PartialChatListViewState.ErrorState())
        stateSubject.onNext(PartialChatListViewState.ErrorState(true))
    }
}
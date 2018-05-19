package com.caldi.chatlist

import com.caldi.chatlist.models.ChatItem
import com.caldi.constants.USERS_NODE
import com.caldi.constants.USER_CHATS_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class ChatListInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun fetchUserChatList(eventId: String, fromChatId: String): Observable<PartialChatListViewState> {
        val stateSubject = PublishSubject.create<PartialChatListViewState>()
        firebaseDatabase.getReference("$USERS_NODE/$currentUserId/$USER_CHATS_NODE/$eventId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        stateSubject.onNext(PartialChatListViewState.SuccessfulChatListFetch(
                                dataSnapshot.children.map { it.getValue(ChatItem::class.java) as ChatItem }))
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    private fun emitError(stateSubject: Subject<PartialChatListViewState>) {
        stateSubject.onNext(PartialChatListViewState.ErrorState())
        stateSubject.onNext(PartialChatListViewState.ErrorState(true))
    }
}
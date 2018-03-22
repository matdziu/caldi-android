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
import java.util.concurrent.TimeUnit

class ChatListInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    fun fetchUserChatList(eventId: String): Observable<PartialChatListViewState> {
        val stateSubject = PublishSubject.create<PartialChatListViewState>()
        firebaseDatabase.getReference("$USERS_NODE/$currentUserId/$USER_CHATS_NODE")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot != null) {
                            stateSubject.onNext(PartialChatListViewState.SuccessfulChatListFetch(
                                    dataSnapshot.children.map {
                                        val chatItem = it.value as ChatItem
                                        chatItem.id = it.key
                                        chatItem
                                    }))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    private fun emitError(stateSubject: Subject<PartialChatListViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialChatListViewState.ErrorState(true) }
                .startWith(PartialChatListViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
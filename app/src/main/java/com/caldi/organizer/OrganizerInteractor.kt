package com.caldi.organizer

import com.caldi.common.utils.NewMessageAddedListener
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.ORGANIZER_MESSAGES_NODE
import com.caldi.constants.TIMESTAMP_CHILD
import com.caldi.organizer.models.EventInfo
import com.caldi.organizer.models.Message
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class OrganizerInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var currentMessagesList = listOf<Message>()

    private val batchSize = 10

    private var newMessageAddedListener: NewMessageAddedListener<Message>? = null

    fun fetchEventInfo(eventId: String): Observable<PartialOrganizerViewState> {
        val stateSubject = PublishSubject.create<PartialOrganizerViewState>()
        firebaseDatabase.getReference("$EVENTS_NODE/$eventId")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        dataSnapshot.getValue(EventInfo::class.java)?.let {
                            stateSubject.onNext(PartialOrganizerViewState.EventInfoFetched(it))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun listenForNewMessages(eventId: String): Observable<PartialOrganizerViewState> {
        val stateSubject = PublishSubject.create<PartialOrganizerViewState>()

        newMessageAddedListener = object : NewMessageAddedListener<Message>(Message::class.java) {
            override fun onNewMessageAdded(newMessage: Message) {
                currentMessagesList += newMessage
                stateSubject.onNext(PartialOrganizerViewState.MessagesListChanged(currentMessagesList))
            }
        }

        getOrganizerMessagesNodeRef(eventId).addChildEventListener(newMessageAddedListener)

        return stateSubject
    }

    fun fetchMessagesBatch(eventId: String, fromTimestamp: String): Observable<PartialOrganizerViewState> {
        val stateSubject = PublishSubject.create<PartialOrganizerViewState>()
        getOrganizerMessagesNodeRef(eventId)
                .orderByChild(TIMESTAMP_CHILD)
                .endAt(fromTimestamp)
                .limitToLast(batchSize)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val messagesBatchList = arrayListOf<Message>()
                        for (messageSnapshot in dataSnapshot.children.toList()) {
                            messageSnapshot.getValue(Message::class.java)?.let { messagesBatchList.add(it) }
                        }
                        currentMessagesList = (messagesBatchList + currentMessagesList).distinct()
                        stateSubject.onNext(PartialOrganizerViewState.MessagesListChanged(currentMessagesList))
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun stopListeningForNewMessages(eventId: String): Observable<PartialOrganizerViewState> {
        newMessageAddedListener?.let { getOrganizerMessagesNodeRef(eventId).removeEventListener(it) }
        return Observable.just(PartialOrganizerViewState.NewMessagesListenerRemoved())
    }

    private fun getOrganizerMessagesNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$ORGANIZER_MESSAGES_NODE")
    }

    private fun emitError(stateSubject: Subject<PartialOrganizerViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialOrganizerViewState.ErrorState(true) }
                .startWith(PartialOrganizerViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
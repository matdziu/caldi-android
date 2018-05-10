package com.caldi.home

import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.NOTIFICATION_TOKEN_CHILD
import com.caldi.constants.USERS_NODE
import com.caldi.constants.USER_EVENTS_NODE
import com.caldi.home.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class HomeInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userEventsNodeRef =
            firebaseDatabase.getReference("$USERS_NODE/${firebaseAuth.currentUser?.uid}/$USER_EVENTS_NODE")

    fun fetchUserEvents(): Observable<PartialHomeViewState> {
        val stateSubject: Subject<PartialHomeViewState> = PublishSubject.create()
        fetchEventIds(stateSubject)
        return stateSubject
    }

    fun saveNotificationToken(): Observable<PartialHomeViewState> {
        val resultSubject = PublishSubject.create<PartialHomeViewState>()
        val currentUserId = firebaseAuth.uid
        val notificationTokenNodeRef = FirebaseDatabase.getInstance().getReference(
                "$USERS_NODE/$currentUserId/$NOTIFICATION_TOKEN_CHILD")
        notificationTokenNodeRef.setValue(FirebaseInstanceId.getInstance().token)
                .addOnSuccessListener { resultSubject.onNext(PartialHomeViewState.NotificationTokenSaveSuccess()) }
        return resultSubject
    }

    private fun fetchEventIds(stateSubject: Subject<PartialHomeViewState>) {
        userEventsNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eventIdList = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(String::class.java)?.let {
                        eventIdList.add(it)
                        FirebaseMessaging.getInstance().subscribeToTopic(it)
                    }
                }

                if (eventIdList.isEmpty()) {
                    stateSubject.onNext(PartialHomeViewState.FetchingSucceeded())
                } else {
                    fetchEvents(eventIdList, stateSubject)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                emitError(stateSubject)
            }
        })
    }

    private fun fetchEvents(eventIdList: List<String>,
                            stateSubject: Subject<PartialHomeViewState>) {
        val eventList = arrayListOf<Event>()
        eventIdList.map { firebaseDatabase.getReference("$EVENTS_NODE/$it") }
                .forEach { eventNodeRef ->
                    eventNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            dataSnapshot.getValue(Event::class.java)?.let {
                                it.id = dataSnapshot.key
                                eventList.add(it)
                            }
                            stateSubject.onNext(PartialHomeViewState.FetchingSucceeded(eventList))
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            emitError(stateSubject)
                        }
                    })
                }
    }

    private fun emitError(stateSubject: Subject<PartialHomeViewState>) {
        stateSubject.onNext(PartialHomeViewState.ErrorState())
        stateSubject.onNext(PartialHomeViewState.ErrorState(true))
    }
}
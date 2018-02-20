package com.caldi.home

import com.caldi.home.models.Event
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class HomeInteractor {

    private val eventsNode = "events"
    private val userEventsNode = "events"
    private val usersNode = "users"

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun fetchUserEvents(): Observable<PartialHomeViewState> {
        val stateSubject: Subject<PartialHomeViewState> = PublishSubject.create()
        val userEventsNodeRef =
                firebaseDatabase.getReference("$usersNode/${firebaseAuth.currentUser?.uid}/$userEventsNode")

        userEventsNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val eventIdList = arrayListOf<String>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(String::class.java)?.let {
                        eventIdList.add(it)
                    }
                }

                if (eventIdList.isEmpty()) stateSubject.onNext(PartialHomeViewState.FetchingSucceeded())

                val eventList = arrayListOf<Event>()
                eventIdList
                        .map { firebaseDatabase.getReference("$eventsNode/$it") }
                        .forEach { eventNodeRef ->
                            eventNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    dataSnapshot.getValue(Event::class.java)?.let {
                                        eventList.add(it)
                                    }
                                    stateSubject.onNext(PartialHomeViewState.FetchingSucceeded(eventList))
                                }

                                override fun onCancelled(dataSnapshot: DatabaseError) {
                                    emitError(stateSubject)
                                }
                            })
                        }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {
                emitError(stateSubject)
            }
        })
        return stateSubject
    }

    private fun emitError(stateSubject: Subject<PartialHomeViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialHomeViewState.ErrorState(true) }
                .startWith(PartialHomeViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
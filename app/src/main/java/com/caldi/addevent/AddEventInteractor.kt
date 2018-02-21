package com.caldi.addevent

import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.EVENT_CODE_CHILD
import com.caldi.constants.USERS_NODE
import com.caldi.constants.USER_EVENTS_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class AddEventInteractor {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userEventsNodeRef =
            firebaseDatabase.getReference("$USERS_NODE/${firebaseAuth.currentUser?.uid}/$USER_EVENTS_NODE")
    private val eventsNode = firebaseDatabase.getReference(EVENTS_NODE)

    fun addNewEvent(eventCode: String): Observable<PartialAddEventViewState> {
        val stateSubject: Subject<PartialAddEventViewState> = PublishSubject.create()
        searchInEventsNode(eventCode, stateSubject)
        return stateSubject
    }

    private fun searchInEventsNode(eventCode: String, stateSubject: Subject<PartialAddEventViewState>) {
        eventsNode.orderByChild(EVENT_CODE_CHILD)
                .equalTo(eventCode)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (childSnapshot in dataSnapshot.children) {
                            searchInUserEventsNode(childSnapshot.key, stateSubject)
                            return
                        }
                        emitError(stateSubject)
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
    }

    private fun searchInUserEventsNode(eventId: String, stateSubject: Subject<PartialAddEventViewState>) {
        userEventsNodeRef
                .orderByValue()
                .equalTo(eventId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            emitError(stateSubject)
                        } else {
                            saveEventIdToUser(eventId, stateSubject)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        emitError(stateSubject)
                    }
                })
    }

    private fun saveEventIdToUser(eventId: String, stateSubject: Subject<PartialAddEventViewState>) {
        userEventsNodeRef
                .push()
                .setValue(eventId)
                .addOnCompleteListener {
                    stateSubject.onNext(PartialAddEventViewState.SuccessState())
                }
    }

    private fun emitError(stateSubject: Subject<PartialAddEventViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialAddEventViewState.ErrorState(true) }
                .startWith(PartialAddEventViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
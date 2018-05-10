package com.caldi.addevent

import com.caldi.constants.ATTENDEES_NODE
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.EVENT_CODE_CHILD
import com.caldi.constants.USERS_NODE
import com.caldi.constants.USER_EVENTS_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class AddEventInteractor {

    private val firebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val userEventsNodeRef =
            firebaseDatabase.getReference("$USERS_NODE/${firebaseAuth.currentUser?.uid}/$USER_EVENTS_NODE")
    private val eventsNodeRef = firebaseDatabase.getReference(EVENTS_NODE)

    fun addNewEvent(eventCode: String): Observable<PartialAddEventViewState> {
        val stateSubject: Subject<PartialAddEventViewState> = PublishSubject.create()
        searchInEventsNode(eventCode, stateSubject)
        return stateSubject
    }

    private fun searchInEventsNode(eventCode: String, stateSubject: Subject<PartialAddEventViewState>) {
        eventsNodeRef.orderByChild(EVENT_CODE_CHILD)
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
                            Observable.zip(saveEventIdToUser(eventId),
                                    saveUserIdToEvent(firebaseAuth.currentUser?.uid, eventId),
                                    BiFunction<Boolean, Boolean, Boolean>
                                    { saveEventIdSuccess, saveUserIdSuccess -> saveEventIdSuccess && saveUserIdSuccess })
                                    .filter { it }
                                    .subscribe { stateSubject.onNext(PartialAddEventViewState.SuccessState()) }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        emitError(stateSubject)
                    }
                })
    }

    private fun saveEventIdToUser(eventId: String): Observable<Boolean> {
        val successSubject = PublishSubject.create<Boolean>()
        userEventsNodeRef
                .push()
                .setValue(eventId)
                .addOnCompleteListener { successSubject.onNext(true) }
        return successSubject
    }

    private fun saveUserIdToEvent(userId: String?, eventId: String): Observable<Boolean> {
        val successSubject = PublishSubject.create<Boolean>()
        subscribeUserToOrganizerMessages(eventId)
        eventsNodeRef
                .child(eventId)
                .child(ATTENDEES_NODE)
                .push()
                .setValue(userId)
                .addOnCompleteListener { successSubject.onNext(true) }
        return successSubject
    }

    private fun subscribeUserToOrganizerMessages(eventId: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(eventId)
    }

    private fun emitError(stateSubject: Subject<PartialAddEventViewState>) {
        stateSubject.onNext(PartialAddEventViewState.ErrorState())
        stateSubject.onNext(PartialAddEventViewState.ErrorState(true))
    }
}
package com.caldi.people.common

import com.caldi.base.BaseProfileInteractor
import com.caldi.common.models.EventProfileData
import com.caldi.constants.ATTENDEES_WITH_PROFILE_NODE
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.MEET_NODE
import com.caldi.constants.NEGATIVE_MEET_NODE
import com.caldi.constants.POSITIVE_MEET_NODE
import com.caldi.constants.USERS_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class PeopleInteractor : BaseProfileInteractor() {

    enum class MeetType { POSITIVE, NEGATIVE }

    private val profilesBatchSize = 5

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    fun checkIfEventProfileIsFilled(eventId: String): Observable<PartialPeopleViewState> {
        val stateSubject = PublishSubject.create<PartialPeopleViewState>()
        getAttendeesWithProfileNodeRef(eventId)
                .orderByValue()
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (!dataSnapshot.hasChildren()) {
                            stateSubject.onNext(PartialPeopleViewState.BlankEventProfileState())
                            stateSubject.onNext(PartialPeopleViewState.BlankEventProfileState(true))
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun saveMetAttendee(metAttendeeId: String, eventId: String, meetType: MeetType)
            : Observable<PartialPeopleViewState> {
        val stateSubject = PublishSubject.create<PartialPeopleViewState>()
        when (meetType) {
            MeetType.POSITIVE -> getMeetNodeRef(eventId).child(POSITIVE_MEET_NODE)
            MeetType.NEGATIVE -> getMeetNodeRef(eventId).child(NEGATIVE_MEET_NODE)
        }
                .push()
                .setValue(metAttendeeId)
                .addOnCompleteListener { stateSubject.onNext(PartialPeopleViewState.SuccessfulMetAttendeeSave()) }
        return stateSubject
    }

    fun fetchAttendeesProfiles(eventId: String,
                               fromUserId: String): Observable<PartialPeopleViewState> {
        val stateSubject = PublishSubject.create<PartialPeopleViewState>()
        fetchAttendeesIdsList(fromUserId, eventId, stateSubject)
        return stateSubject
    }

    private fun fetchAttendeesIdsList(fromUserId: String,
                                      eventId: String,
                                      stateSubject: Subject<PartialPeopleViewState>) {
        getAttendeesWithProfileNodeRef(eventId)
                .orderByValue()
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot != null) {
                            val attendeesIdsList = dataSnapshot.children
                                    .map { it.value as String }
                                    .filter { it != currentUserId }

                            fetchUnmetAttendeesProfiles(attendeesIdsList,
                                    fromUserId,
                                    eventId,
                                    stateSubject)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
    }

    private fun fetchUnmetAttendeesProfiles(attendeesIdsList: List<String>,
                                            fromUserId: String,
                                            eventId: String,
                                            stateSubject: Subject<PartialPeopleViewState>) {
        val notMetAttendeesList = arrayListOf<EventProfileData>()
        var notMetAttendeesNumber = 0

        Observable.zip(
                fetchMetAttendeesIdsList(eventId, MeetType.POSITIVE),
                fetchMetAttendeesIdsList(eventId, MeetType.NEGATIVE),
                BiFunction<List<String>, List<String>, List<String>>
                { positiveAttendeesList, negativeAttendeesList ->
                    positiveAttendeesList + negativeAttendeesList
                })
                .map { metAttendeesIdsList ->
                    (attendeesIdsList - metAttendeesIdsList)
                            .takeLastWhile { it > fromUserId }
                            .take(profilesBatchSize)
                }
                .doOnNext {
                    notMetAttendeesNumber = it.size
                    if (notMetAttendeesNumber == 0) {
                        stateSubject.onNext(
                                PartialPeopleViewState.SuccessfulAttendeesFetchState()
                        )
                    }
                }
                .flatMapIterable { it }
                .flatMap { fetchEventProfileData(eventId, it) }
                .subscribe {
                    notMetAttendeesList.add(it)
                    if (notMetAttendeesList.size == notMetAttendeesNumber) {
                        stateSubject.onNext(
                                PartialPeopleViewState.SuccessfulAttendeesFetchState(notMetAttendeesList)
                        )
                    }
                }
    }

    private fun fetchMetAttendeesIdsList(eventId: String, meetType: MeetType): Observable<List<String>> {
        val resultSubject = PublishSubject.create<List<String>>()

        when (meetType) {
            MeetType.POSITIVE -> getMeetNodeRef(eventId).child(POSITIVE_MEET_NODE)
            MeetType.NEGATIVE -> getMeetNodeRef(eventId).child(NEGATIVE_MEET_NODE)
        }
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot != null) {
                            val metAttendeesIdsList = dataSnapshot.children.map { it.value as String }
                            resultSubject.onNext(metAttendeesIdsList)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        resultSubject.onNext(listOf())
                    }
                })

        return resultSubject
    }

    private fun getAttendeesWithProfileNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$ATTENDEES_WITH_PROFILE_NODE")
    }

    private fun getMeetNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$currentUserId/$MEET_NODE/$eventId")
    }

    private fun emitError(stateSubject: Subject<PartialPeopleViewState>) {
        stateSubject.onNext(PartialPeopleViewState.ErrorState())
        stateSubject.onNext(PartialPeopleViewState.ErrorState(true))
    }
}
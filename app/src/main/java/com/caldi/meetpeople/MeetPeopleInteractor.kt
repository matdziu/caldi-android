package com.caldi.meetpeople

import com.caldi.base.BaseProfileInteractor
import com.caldi.common.models.Answer
import com.caldi.common.models.Question
import com.caldi.constants.ATTENDEES_WITH_PROFILE_NODE
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.EVENT_PROFILE_NODE
import com.caldi.constants.MEET_NODE
import com.caldi.constants.NEGATIVE_MEET_NODE
import com.caldi.constants.POSITIVE_MEET_NODE
import com.caldi.constants.USERS_NODE
import com.caldi.meetpeople.models.AttendeeProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class MeetPeopleInteractor : BaseProfileInteractor() {

    enum class MeetType { POSITIVE, NEGATIVE }

    private val profilesBatchSize = 5

    private var cachedAttendeesIdsList = listOf<String>()

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    fun checkIfEventProfileIsFilled(eventId: String): Observable<PartialMeetPeopleViewState> {
        val stateSubject = PublishSubject.create<PartialMeetPeopleViewState>()
        firebaseDatabase.getReference("$USERS_NODE/$currentUserId/$EVENT_PROFILE_NODE")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot == null || !dataSnapshot.hasChild(eventId)) {
                            PartialMeetPeopleViewState.BlankEventProfileState()
                            PartialMeetPeopleViewState.BlankEventProfileState(true)
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        emitError(stateSubject)
                    }
                })
        return stateSubject
    }

    fun saveMetAttendee(metAttendeeId: String, eventId: String, meetType: MeetType)
            : Observable<PartialMeetPeopleViewState> {
        val stateSubject = PublishSubject.create<PartialMeetPeopleViewState>()
        when (meetType) {
            MeetType.POSITIVE -> getMeetNodeRef(eventId).child(POSITIVE_MEET_NODE)
            MeetType.NEGATIVE -> getMeetNodeRef(eventId).child(NEGATIVE_MEET_NODE)
        }
                .push()
                .setValue(metAttendeeId)
                .addOnCompleteListener { stateSubject.onNext(PartialMeetPeopleViewState.SuccessfulMetAttendeeSave()) }
        return stateSubject
    }

    fun fetchAttendeesProfiles(eventId: String): Observable<PartialMeetPeopleViewState> {
        val stateSubject = PublishSubject.create<PartialMeetPeopleViewState>()
        fetchAttendeesIdsList(eventId, stateSubject)
        return stateSubject
    }

    private fun fetchAttendeesIdsList(eventId: String, stateSubject: Subject<PartialMeetPeopleViewState>) {
        if (cachedAttendeesIdsList.isEmpty()) {
            getAttendeesWithProfileNodeRef(eventId)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot?) {
                            if (dataSnapshot != null) {
                                cachedAttendeesIdsList = dataSnapshot.children
                                        .map { it.value as String }
                                        .filter { it != currentUserId }

                                fetchUnmetAttendeesProfiles(eventId, stateSubject)
                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            emitError(stateSubject)
                        }
                    })
        } else {
            fetchUnmetAttendeesProfiles(eventId, stateSubject)
        }
    }

    private fun fetchUnmetAttendeesProfiles(eventId: String, stateSubject: Subject<PartialMeetPeopleViewState>) {
        val notMetAttendeesList = arrayListOf<AttendeeProfile>()
        var notMetAttendeesNumber = 0

        Observable.zip(
                fetchMetAttendeesIdsList(eventId, MeetType.POSITIVE),
                fetchMetAttendeesIdsList(eventId, MeetType.NEGATIVE),
                BiFunction<List<String>, List<String>, List<String>>
                { positiveAttendeesList, negativeAttendeesList ->
                    positiveAttendeesList + negativeAttendeesList
                })
                .map { metAttendeesIdsList -> cachedAttendeesIdsList - metAttendeesIdsList }
                .map { it.takeLast(profilesBatchSize) }
                .doOnNext {
                    notMetAttendeesNumber = it.size
                    if (notMetAttendeesNumber == 0) {
                        stateSubject.onNext(
                                PartialMeetPeopleViewState.SuccessfulAttendeesFetchState()
                        )
                    }
                }
                .flatMapIterable { it }
                .flatMap { fetchAttendeeProfile(eventId, it) }
                .subscribe {
                    notMetAttendeesList.add(it)
                    if (notMetAttendeesList.size == notMetAttendeesNumber) {
                        stateSubject.onNext(
                                PartialMeetPeopleViewState.SuccessfulAttendeesFetchState(notMetAttendeesList)
                        )
                    }
                }
    }

    private fun fetchAttendeeProfile(eventId: String, userId: String): Observable<AttendeeProfile> {
        return Observable.zip(
                fetchQuestions(eventId),
                fetchAnswers(eventId, userId),
                fetchEventUserName(eventId, userId),
                fetchEventProfilePictureUrl(eventId, userId),
                Function4<List<Question>, List<Answer>, String, String, AttendeeProfile>
                { questionList, answerList, eventUserName, profilePictureUrl ->
                    AttendeeProfile(userId, eventUserName, profilePictureUrl, answerList, questionList)
                })
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

    private fun emitError(stateSubject: Subject<PartialMeetPeopleViewState>) {
        stateSubject.onNext(PartialMeetPeopleViewState.ErrorState())
        stateSubject.onNext(PartialMeetPeopleViewState.ErrorState(true))
    }
}
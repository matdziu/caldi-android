package com.caldi.meetpeople

import com.caldi.base.BaseProfileInteractor
import com.caldi.base.models.Answer
import com.caldi.base.models.Question
import com.caldi.constants.ATTENDEES_WITH_PROFILE_NODE
import com.caldi.constants.EVENTS_NODE
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
import io.reactivex.functions.Function4
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class MeetPeopleInteractor : BaseProfileInteractor() {

    enum class MeetType { POSITIVE, NEGATIVE }

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    fun saveMetAttendee(metAttendeeId: String, eventId: String, meetType: MeetType)
            : Observable<PartialMeetPeopleViewState> {
        val stateSubject = PublishSubject.create<PartialMeetPeopleViewState>()
        var meetNodeRef = getMeetNodeRef(eventId)
        meetNodeRef = when (meetType) {
            MeetType.POSITIVE -> meetNodeRef.child(POSITIVE_MEET_NODE)
            MeetType.NEGATIVE -> meetNodeRef.child(NEGATIVE_MEET_NODE)
        }
        meetNodeRef
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
        getAttendeesWithProfileNodeRef(eventId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot != null) {
                            val attendeesIdsList = dataSnapshot.children
                                    .map { it.value as String }
                                    .filter { it != currentUserId }

                            val attendeesProfilesList = arrayListOf<AttendeeProfile>()
                            for ((index, attendeeId) in attendeesIdsList.withIndex()) {
                                fetchAttendeeProfile(eventId, attendeeId).subscribe {
                                    attendeesProfilesList.add(it)
                                    if (index == attendeesIdsList.size - 1) {
                                        stateSubject.onNext(PartialMeetPeopleViewState.SuccessfulProfileFetchState(attendeesProfilesList))
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        emitError(stateSubject)
                    }
                })
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

    private fun getAttendeesWithProfileNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$ATTENDEES_WITH_PROFILE_NODE")
    }

    private fun getMeetNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$currentUserId/$MEET_NODE/$eventId")
    }

    private fun emitError(stateSubject: Subject<PartialMeetPeopleViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialMeetPeopleViewState.ErrorState(true) }
                .startWith(PartialMeetPeopleViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
package com.caldi.eventprofile

import android.net.Uri
import com.caldi.base.BaseProfileInteractor
import com.caldi.common.models.Answer
import com.caldi.common.models.Question
import com.caldi.constants.ATTENDEES_WITH_PROFILE_NODE
import com.caldi.constants.EVENTS_NODE
import com.caldi.eventprofile.models.EventProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import io.reactivex.functions.Function4
import io.reactivex.functions.Function5
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File

class EventProfileInteractor : BaseProfileInteractor() {

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId = firebaseAuth.currentUser?.uid ?: ""

    fun fetchEventProfile(eventId: String): Observable<PartialEventProfileViewState> {
        return Observable.zip(
                fetchQuestions(eventId),
                fetchAnswers(eventId, currentUserId),
                fetchEventUserName(eventId, currentUserId),
                fetchEventProfilePictureUrl(eventId, currentUserId),
                fetchUserLinkUrl(eventId, currentUserId),
                Function5<List<Question>, List<Answer>, String, String, String, EventProfileData>
                { questionList, answerList, eventUserName, profilePictureUrl, userLinkUrl ->
                    EventProfileData(eventUserName, answerList, questionList, profilePictureUrl, userLinkUrl)
                })
                .flatMap { emitSuccessfulFetchState(it) }
    }

    fun updateEventProfile(eventId: String, eventProfileData: EventProfileData): Observable<PartialEventProfileViewState> {
        return Observable.zip(
                updateEventUserName(eventId, eventProfileData.eventUserName),
                updateAnswers(eventId, eventProfileData.answerList),
                saveUserIdToAttendeesWithProfile(eventId),
                updateUserLinkUrl(eventId, eventProfileData.userLinkUrl),
                Function4<Boolean, Boolean, Boolean, Boolean, Boolean>
                { successUpdateName, successUpdateAnswers, successSavingAttendee, successUpdateUserLinkUrl ->
                    successUpdateName && successUpdateAnswers && successSavingAttendee && successUpdateUserLinkUrl
                })
                .flatMap { if (it) emitSuccessfulUpdateState() else emitError() }
    }

    private fun updateEventUserName(eventId: String, eventUserName: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        val userEventNameRef = getEventUserNameNodeRef(eventId, currentUserId)
        userEventNameRef.setValue(eventUserName).addOnCompleteListener { resultSubject.onNext(true) }
        return resultSubject
    }

    private fun updateUserLinkUrl(eventId: String, userLinkUrl: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        val userLinkUrlRef = getUserLinkUrlNodeRef(eventId, currentUserId)
        userLinkUrlRef.setValue(userLinkUrl).addOnCompleteListener { resultSubject.onNext(true) }
        return resultSubject
    }

    private fun updateAnswers(eventId: String, answerList: List<Answer>): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        val userAnswersNodeRef = getUserAnswersNodeRef(eventId, currentUserId)

        val requiredSuccessfulUpdates = answerList.size
        var currentSuccessfulUpdates = 0
        for (answer in answerList) {
            userAnswersNodeRef.child(answer.questionId).setValue(answer.answer).addOnCompleteListener {
                currentSuccessfulUpdates++
                if (currentSuccessfulUpdates == requiredSuccessfulUpdates) {
                    resultSubject.onNext(true)
                }
            }
        }
        return resultSubject
    }

    private fun saveUserIdToAttendeesWithProfile(eventId: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        val attendeesWithProfileNodeRef =
                firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$ATTENDEES_WITH_PROFILE_NODE")

        attendeesWithProfileNodeRef
                .orderByValue()
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                            resultSubject.onNext(true)
                        } else {
                            attendeesWithProfileNodeRef
                                    .push()
                                    .setValue(currentUserId)
                                    .addOnCompleteListener { resultSubject.onNext(true) }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        resultSubject.onNext(false)
                    }
                })

        return resultSubject
    }

    fun uploadProfilePicture(eventId: String, profilePicture: File): Observable<PartialEventProfileViewState> {
        val resultSubject = PublishSubject.create<PartialEventProfileViewState>()
        val profilePictureUri = Uri.fromFile(profilePicture)
        firebaseAuth.uid?.let {
            firebaseStorage.reference.child("$eventId/$it").putFile(profilePictureUri)
                    .addOnFailureListener { emitError(resultSubject) }
                    .addOnSuccessListener { updateEventProfilePictureNode(eventId, it.downloadUrl.toString(), resultSubject) }
        }
        return resultSubject
    }

    private fun updateEventProfilePictureNode(eventId: String, profilePictureUrl: String,
                                              resultSubject: Subject<PartialEventProfileViewState>) {
        getEventProfilePictureNodeRef(eventId, currentUserId).setValue(profilePictureUrl)
                .addOnCompleteListener {
                    resultSubject.onNext(
                            PartialEventProfileViewState.SuccessfulPictureUploadState(profilePictureUrl)
                    )
                }
                .addOnFailureListener { emitError(resultSubject) }
    }

    private fun emitSuccessfulFetchState(eventProfileData: EventProfileData): Observable<PartialEventProfileViewState> {
        return Observable.just(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, false))
                .startWith(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, true))
                as Observable<PartialEventProfileViewState>
    }

    private fun emitSuccessfulUpdateState(): Observable<PartialEventProfileViewState> {
        return Observable.just(PartialEventProfileViewState.SuccessfulUpdateState(true))
                .startWith(PartialEventProfileViewState.SuccessfulUpdateState())
                as Observable<PartialEventProfileViewState>
    }

    private fun emitError(): Observable<PartialEventProfileViewState> {
        return Observable.just(PartialEventProfileViewState.ErrorState(true))
                .startWith(PartialEventProfileViewState.ErrorState())
                as Observable<PartialEventProfileViewState>
    }

    private fun emitError(resultSubject: Subject<PartialEventProfileViewState>) {
        resultSubject.onNext(PartialEventProfileViewState.ErrorState())
        resultSubject.onNext(PartialEventProfileViewState.ErrorState(true))
    }
}
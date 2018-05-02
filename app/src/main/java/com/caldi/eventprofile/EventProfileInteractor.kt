package com.caldi.eventprofile

import android.net.Uri
import com.caldi.base.BaseProfileInteractor
import com.caldi.common.models.EventProfileData
import com.caldi.constants.ATTENDEES_WITH_PROFILE_NODE
import com.caldi.constants.EVENTS_NODE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File

class EventProfileInteractor : BaseProfileInteractor() {

    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val currentUserId = firebaseAuth.currentUser?.uid ?: ""

    fun fetchEventProfile(eventId: String): Observable<PartialEventProfileViewState> {
        return Observable.zip<EventProfileData, Map<String, String>, PartialEventProfileViewState.SuccessfulFetchState>(
                fetchEventProfileData(eventId, currentUserId),
                fetchQuestions(eventId),
                BiFunction { eventProfileData, questions ->
                    PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, questions)
                })
                .flatMap { Observable.just(it.copy(renderInputs = false)).startWith(it) }
    }

    fun updateEventProfile(eventId: String, eventProfileData: EventProfileData): Observable<PartialEventProfileViewState> {
        val resultSubject = PublishSubject.create<PartialEventProfileViewState>()
        val eventProfileNodeRef = getEventProfileNodeRef(eventId, currentUserId)
        eventProfileNodeRef.setValue(eventProfileData)
                .continueWith {
                    if (it.isSuccessful) {
                        saveUserIdToAttendeesWithProfile(eventId, resultSubject)
                    } else {
                        emitError(resultSubject)
                    }
                }
        return resultSubject
    }

    private fun saveUserIdToAttendeesWithProfile(eventId: String, resultSubject: Subject<PartialEventProfileViewState>) {
        val attendeesWithProfileNodeRef =
                firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$ATTENDEES_WITH_PROFILE_NODE")

        attendeesWithProfileNodeRef
                .orderByValue()
                .equalTo(currentUserId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot?) {
                        if (dataSnapshot == null || !dataSnapshot.hasChildren()) {
                            attendeesWithProfileNodeRef
                                    .push()
                                    .setValue(currentUserId)
                        }
                        emitSuccessfulUpdateState(resultSubject)
                    }

                    override fun onCancelled(databaseError: DatabaseError?) {
                        emitError(resultSubject)
                    }
                })
    }

    fun uploadProfilePicture(eventId: String, profilePicture: File): Observable<PartialEventProfileViewState> {
        val resultSubject = PublishSubject.create<PartialEventProfileViewState>()
        val profilePictureUri = Uri.fromFile(profilePicture)
        firebaseStorage.reference.child("$eventId/$currentUserId").putFile(profilePictureUri)
                .addOnSuccessListener { updateEventProfilePictureNode(eventId, it.downloadUrl.toString(), resultSubject) }
                .addOnFailureListener { emitError(resultSubject) }
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

    private fun emitSuccessfulUpdateState(resultSubject: Subject<PartialEventProfileViewState>) {
        resultSubject.onNext(PartialEventProfileViewState.SuccessfulUpdateState())
        resultSubject.onNext(PartialEventProfileViewState.SuccessfulUpdateState(true))
    }

    private fun emitError(resultSubject: Subject<PartialEventProfileViewState>) {
        resultSubject.onNext(PartialEventProfileViewState.ErrorState())
        resultSubject.onNext(PartialEventProfileViewState.ErrorState(true))
    }
}
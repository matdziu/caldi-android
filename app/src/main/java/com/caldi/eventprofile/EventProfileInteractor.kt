package com.caldi.eventprofile

import android.net.Uri
import com.caldi.constants.ANSWERS_NODE
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.EVENT_PROFILE_NODE
import com.caldi.constants.EVENT_USER_NAME_CHILD
import com.caldi.constants.PROFILE_PICTURE_CHILD
import com.caldi.constants.QUESTIONS_NODE
import com.caldi.constants.USERS_NODE
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.EventProfileData
import com.caldi.eventprofile.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function4
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.io.File
import java.util.concurrent.TimeUnit

class EventProfileInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseStorage: FirebaseStorage = FirebaseStorage.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun fetchEventProfile(eventId: String): Observable<PartialEventProfileViewState> {
        return Observable.zip(fetchQuestions(eventId), fetchAnswers(eventId),
                fetchEventUserName(eventId), fetchEventProfilePictureUrl(eventId),
                Function4<List<Question>, List<Answer>, String, String, EventProfileData>
                { questionList, answerList, eventUserName, profilePictureUrl ->
                    EventProfileData(eventUserName, answerList, questionList, profilePictureUrl)
                })
                .flatMap { emitSuccessfulFetchState(it) }
    }

    private fun fetchQuestions(eventId: String): Observable<List<Question>> {
        val resultSubject = PublishSubject.create<List<Question>>()
        val questionsNodeRef = getEventQuestionsNodeRef(eventId)
        questionsNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val questionList = dataSnapshot.children.map { Question(it.key, it.value as String) }
                resultSubject.onNext(questionList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onNext(listOf())
            }
        })
        return resultSubject
    }

    private fun fetchAnswers(eventId: String): Observable<List<Answer>> {
        val resultSubject = PublishSubject.create<List<Answer>>()
        val userAnswersNodeRef = getUserAnswersNodeRef(eventId)
        userAnswersNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot != null) {
                    val answersList = dataSnapshot.children.map { Answer(it.key, it.value as String) }
                    resultSubject.onNext(answersList)
                } else {
                    resultSubject.onNext(listOf())
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onNext(listOf())
            }
        })
        return resultSubject
    }

    private fun fetchEventUserName(eventId: String): Observable<String> {
        val resultSubject = PublishSubject.create<String>()
        val eventUserNameNodeRef = getEventUserNameNodeRef(eventId)
        eventUserNameNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot?.value != null) {
                    resultSubject.onNext(dataSnapshot.value as String)
                } else {
                    resultSubject.onNext("")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onNext("")
            }
        })
        return resultSubject
    }

    private fun fetchEventProfilePictureUrl(eventId: String): Observable<String> {
        val resultSubject = PublishSubject.create<String>()
        val eventProfilePictureNode = getEventProfilePictureNodeRef(eventId)
        eventProfilePictureNode.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                if (dataSnapshot?.value != null) {
                    resultSubject.onNext(dataSnapshot.value as String)
                } else {
                    resultSubject.onNext("")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onNext("")
            }
        })
        return resultSubject
    }

    fun updateEventProfile(eventId: String, eventProfileData: EventProfileData): Observable<PartialEventProfileViewState> {
        return Observable.zip(updateEventUserName(eventId, eventProfileData.eventUserName), updateAnswers(eventId, eventProfileData.answerList),
                BiFunction<Boolean, Boolean, Boolean> { successUpdateName, successUpdateAnswers ->
                    successUpdateName && successUpdateAnswers
                })
                .flatMap { if (it) emitSuccessfulUpdateState() else emitError() }
    }

    private fun updateEventUserName(eventId: String, eventUserName: String): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        val userEventNameRef = getEventUserNameNodeRef(eventId)
        userEventNameRef.setValue(eventUserName).addOnCompleteListener { resultSubject.onNext(true) }
        return resultSubject
    }

    private fun updateAnswers(eventId: String, answerList: List<Answer>): Observable<Boolean> {
        val resultSubject = PublishSubject.create<Boolean>()
        val userAnswersNodeRef = getUserAnswersNodeRef(eventId)

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
        getEventProfilePictureNodeRef(eventId).setValue(profilePictureUrl)
                .addOnCompleteListener {
                    resultSubject.onNext(
                            PartialEventProfileViewState.SuccessfulPictureUploadState(profilePictureUrl)
                    )
                }
                .addOnFailureListener { emitError(resultSubject) }
    }

    private fun getUserAnswersNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference(
                "$USERS_NODE/${firebaseAuth.currentUser?.uid}/$EVENT_PROFILE_NODE/$eventId/$ANSWERS_NODE")
    }

    private fun getEventUserNameNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference(
                "$USERS_NODE/${firebaseAuth.currentUser?.uid}/$EVENT_PROFILE_NODE/$eventId/$EVENT_USER_NAME_CHILD")
    }

    private fun getEventQuestionsNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$QUESTIONS_NODE")
    }

    private fun getEventProfilePictureNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference(
                "$USERS_NODE/${firebaseAuth.currentUser?.uid}/$EVENT_PROFILE_NODE/$eventId/$PROFILE_PICTURE_CHILD")
    }

    private fun emitSuccessfulFetchState(eventProfileData: EventProfileData): Observable<PartialEventProfileViewState> {
        return Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, false) }
                .startWith(PartialEventProfileViewState.SuccessfulFetchState(eventProfileData, true))
                as Observable<PartialEventProfileViewState>
    }

    private fun emitSuccessfulUpdateState(): Observable<PartialEventProfileViewState> {
        return Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialEventProfileViewState.SuccessfulUpdateState(true) }
                .startWith(PartialEventProfileViewState.SuccessfulUpdateState()) as Observable<PartialEventProfileViewState>
    }

    private fun emitError(): Observable<PartialEventProfileViewState> {
        return Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialEventProfileViewState.ErrorState(true) }
                .startWith(PartialEventProfileViewState.ErrorState()) as Observable<PartialEventProfileViewState>
    }

    private fun emitError(resultSubject: Subject<PartialEventProfileViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialEventProfileViewState.ErrorState(true) }
                .startWith(PartialEventProfileViewState.ErrorState())
                .subscribe(resultSubject)
    }
}
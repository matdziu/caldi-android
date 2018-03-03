package com.caldi.eventprofile

import com.caldi.constants.ANSWERS_NODE
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.QUESTIONS_NODE
import com.caldi.constants.USERS_NODE
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.Question
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class EventProfileInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun fetchQuestions(eventId: String): Observable<PartialEventProfileViewState> {
        val stateSubject = PublishSubject.create<PartialEventProfileViewState>()
        val questionsNodeRef = firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$QUESTIONS_NODE")
        questionsNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val questionList = dataSnapshot.children.map { Question(it.key, it.value as String) }
                fetchAnswers(eventId, questionList, stateSubject)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                emitError(stateSubject)
            }
        })
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }

    private fun fetchAnswers(eventId: String, questionList: List<Question>,
                             stateSubject: Subject<PartialEventProfileViewState>) {
        val userEventAnswersNode
                = firebaseDatabase.getReference("$USERS_NODE/${firebaseAuth.currentUser?.uid}/$ANSWERS_NODE/$eventId")
        val requiredSuccessfulReads = questionList.size
        var currentSuccessfulReads = 0
        for (question in questionList) {
            userEventAnswersNode.child(question.id).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot?) {
                    currentSuccessfulReads++
                    dataSnapshot?.value?.let { question.answer = it as String }
                    if (currentSuccessfulReads == requiredSuccessfulReads) {
                        stateSubject.onNext(PartialEventProfileViewState.SuccessfulFetchState(questionList))
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    emitError(stateSubject)
                }
            })
        }
    }

    fun updateAnswers(eventId: String, answerList: List<Answer>): Observable<PartialEventProfileViewState> {
        val stateSubject = PublishSubject.create<PartialEventProfileViewState>()
        val userEventAnswersNode
                = firebaseDatabase.getReference("$USERS_NODE/${firebaseAuth.currentUser?.uid}/$ANSWERS_NODE/$eventId")

        val requiredSuccessfulUploads = answerList.size
        var currentSuccessfulUploads = 0
        for (answer in answerList) {
            userEventAnswersNode.child(answer.questionId).setValue(answer.answer)
                    .addOnCompleteListener {
                        currentSuccessfulUploads++
                        if (currentSuccessfulUploads == requiredSuccessfulUploads) {
                            emitSuccessfulUpdateState(stateSubject)
                        }
                    }
        }
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }

    private fun emitSuccessfulUpdateState(stateSubject: Subject<PartialEventProfileViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialEventProfileViewState.SuccessfulAnswersUpdateState(true) }
                .startWith(PartialEventProfileViewState.SuccessfulAnswersUpdateState())
                .subscribe(stateSubject)
    }

    private fun emitError(stateSubject: Subject<PartialEventProfileViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialEventProfileViewState.ErrorState(true) }
                .startWith(PartialEventProfileViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
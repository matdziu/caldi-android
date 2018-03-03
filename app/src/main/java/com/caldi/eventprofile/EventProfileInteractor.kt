package com.caldi.eventprofile

import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.QUESTIONS_NODE
import com.caldi.eventprofile.models.Question
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class EventProfileInteractor {

    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun fetchQuestions(eventId: String): Observable<PartialEventProfileViewState> {
        val stateSubject = PublishSubject.create<PartialEventProfileViewState>()
        val questionsNodeRef = firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$QUESTIONS_NODE")
        questionsNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val questionList = arrayListOf<Question>()
                for (childSnapshot in dataSnapshot.children) {
                    childSnapshot.getValue(Question::class.java)?.let {
                        it.id = childSnapshot.key
                        questionList.add(it)
                    }
                }
                stateSubject.onNext(PartialEventProfileViewState.SuccessState(questionList))
            }

            override fun onCancelled(databaseError: DatabaseError) {
                emitError(stateSubject)
            }
        })
        return stateSubject
    }

    private fun emitError(stateSubject: Subject<PartialEventProfileViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialEventProfileViewState.ErrorState(true) }
                .startWith(PartialEventProfileViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
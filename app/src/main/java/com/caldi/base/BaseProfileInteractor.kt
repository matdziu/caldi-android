package com.caldi.base

import com.caldi.common.models.Answer
import com.caldi.common.models.Question
import com.caldi.constants.ANSWERS_NODE
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.EVENT_PROFILE_NODE
import com.caldi.constants.EVENT_USER_NAME_CHILD
import com.caldi.constants.PROFILE_PICTURE_CHILD
import com.caldi.constants.QUESTIONS_NODE
import com.caldi.constants.USERS_NODE
import com.caldi.constants.USER_LINK_URL_CHILD
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class BaseProfileInteractor {

    protected val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    protected fun fetchQuestions(eventId: String): Observable<List<Question>> {
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

    protected fun fetchAnswers(eventId: String, userId: String): Observable<List<Answer>> {
        val resultSubject = PublishSubject.create<List<Answer>>()
        val userAnswersNodeRef = getUserAnswersNodeRef(eventId, userId)
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

    protected fun fetchEventUserName(eventId: String, userId: String): Observable<String> {
        val resultSubject = PublishSubject.create<String>()
        val eventUserNameNodeRef = getEventUserNameNodeRef(eventId, userId)
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

    protected fun fetchUserLinkUrl(eventId: String, userId: String): Observable<String> {
        val resultSubject = PublishSubject.create<String>()
        val userLinkNodeRef = getUserLinkUrlNodeRef(eventId, userId)
        userLinkNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
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

    protected fun fetchEventProfilePictureUrl(eventId: String, userId: String): Observable<String> {
        val resultSubject = PublishSubject.create<String>()
        val eventProfilePictureNode = getEventProfilePictureNodeRef(eventId, userId)
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

    protected fun getUserAnswersNodeRef(eventId: String, userId: String): DatabaseReference {
        return firebaseDatabase.getReference(
                "$USERS_NODE/$userId/$EVENT_PROFILE_NODE/$eventId/$ANSWERS_NODE")
    }

    protected fun getEventUserNameNodeRef(eventId: String, userId: String): DatabaseReference {
        return firebaseDatabase.getReference(
                "$USERS_NODE/$userId/$EVENT_PROFILE_NODE/$eventId/$EVENT_USER_NAME_CHILD")
    }

    private fun getEventQuestionsNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$QUESTIONS_NODE")
    }

    protected fun getEventProfilePictureNodeRef(eventId: String, userId: String): DatabaseReference {
        return firebaseDatabase.getReference(
                "$USERS_NODE/$userId/$EVENT_PROFILE_NODE/$eventId/$PROFILE_PICTURE_CHILD")
    }

    protected fun getUserLinkUrlNodeRef(eventId: String, userId: String): DatabaseReference {
        return firebaseDatabase.getReference(
                "$USERS_NODE/$userId/$EVENT_PROFILE_NODE/$eventId/$USER_LINK_URL_CHILD")
    }
}
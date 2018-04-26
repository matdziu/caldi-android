package com.caldi.base

import com.caldi.common.models.EventProfileData
import com.caldi.constants.EVENTS_NODE
import com.caldi.constants.EVENT_PROFILE_NODE
import com.caldi.constants.PROFILE_PICTURE_CHILD
import com.caldi.constants.QUESTIONS_NODE
import com.caldi.constants.USERS_NODE
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

open class BaseProfileInteractor {

    protected val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    protected fun fetchQuestions(eventId: String): Observable<Map<String, String>> {
        val resultSubject = PublishSubject.create<Map<String, String>>()
        val questionsNodeRef = getEventQuestionsNodeRef(eventId)
        questionsNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                resultSubject.onNext(dataSnapshot.value as Map<String, String>)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onNext(mapOf())
            }
        })
        return resultSubject
    }

    protected fun fetchEventProfileData(eventId: String, userId: String): Observable<EventProfileData> {
        val resultSubject = PublishSubject.create<EventProfileData>()
        val eventProfileNodeRef = getEventProfileNodeRef(eventId, userId)
        eventProfileNodeRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(EventProfileData::class.java)?.let { resultSubject.onNext(it) }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                resultSubject.onNext(EventProfileData())
            }
        })
        return resultSubject
    }

    protected fun getEventProfileNodeRef(eventId: String, userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId/$EVENT_PROFILE_NODE/$eventId")
    }

    private fun getEventQuestionsNodeRef(eventId: String): DatabaseReference {
        return firebaseDatabase.getReference("$EVENTS_NODE/$eventId/$QUESTIONS_NODE")
    }

    protected fun getEventProfilePictureNodeRef(eventId: String, userId: String): DatabaseReference {
        return firebaseDatabase.getReference("$USERS_NODE/$userId/$EVENT_PROFILE_NODE/$eventId/$PROFILE_PICTURE_CHILD")
    }
}
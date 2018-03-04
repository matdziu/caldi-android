package com.caldi.eventprofile

import com.caldi.eventprofile.models.EventProfileData
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import junit.framework.Assert

class EventProfileViewRobot(eventProfileViewModel: EventProfileViewModel) {

    private val renderedStates = arrayListOf<EventProfileViewState>()

    private val profileFetchingTriggerObservable = PublishSubject.create<String>()

    private val inputDataObservable = PublishSubject.create<Pair<String, EventProfileData>>()

    private val eventProfileView = object : EventProfileView {

        override fun emitEventProfileFetchingTrigger(): Observable<String> = profileFetchingTriggerObservable

        override fun emitInputData(): Observable<Pair<String, EventProfileData>> = inputDataObservable

        override fun render(eventProfileViewState: EventProfileViewState) {
            renderedStates.add(eventProfileViewState)
        }
    }

    init {
        eventProfileViewModel.bind(eventProfileView)
    }

    fun fetchEventProfile(eventId: String) {
        profileFetchingTriggerObservable.onNext(eventId)
    }

    fun emitInputData(eventId: String, eventProfileData: EventProfileData) {
        inputDataObservable.onNext(Pair(eventId, eventProfileData))
    }

    fun assertViewStates(vararg expectedStates: EventProfileViewState) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}
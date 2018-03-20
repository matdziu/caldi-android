package com.caldi.meetpeople

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MeetPeopleViewRobot(meetPeopleViewModel: MeetPeopleViewModel) : BaseViewRobot<MeetPeopleViewState>() {

    private val positiveMeetObservable = PublishSubject.create<String>()

    private val negativeMeetObservable = PublishSubject.create<String>()

    private val profilesFetchingTriggerObservable = PublishSubject.create<String>()

    private val meetPeopleView = object : MeetPeopleView {

        override fun emitPositiveMeet(): Observable<String> = positiveMeetObservable

        override fun emitNegativeMeet(): Observable<String> = negativeMeetObservable

        override fun emitProfilesFetchingTrigger(): Observable<String> = profilesFetchingTriggerObservable

        override fun render(meetPeopleViewState: MeetPeopleViewState) {
            renderedStates.add(meetPeopleViewState)
        }
    }

    init {
        meetPeopleViewModel.bind(meetPeopleView)
    }

    fun triggerProfilesFetching(eventId: String) {
        profilesFetchingTriggerObservable.onNext(eventId)
    }

    fun positiveAttendeeMeet(userId: String) {
        positiveMeetObservable.onNext(userId)
    }

    fun negativeAttendeeMeet(userId: String) {
        negativeMeetObservable.onNext(userId)
    }
}
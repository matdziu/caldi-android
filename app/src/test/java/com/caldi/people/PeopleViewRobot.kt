package com.caldi.people

import com.caldi.base.BaseViewRobot
import com.caldi.people.common.PeopleView
import com.caldi.people.common.PeopleViewModel
import com.caldi.people.common.PeopleViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class PeopleViewRobot(peopleViewModel: PeopleViewModel) : BaseViewRobot<PeopleViewState>() {

    private val positiveMeetObservable = PublishSubject.create<String>()

    private val negativeMeetObservable = PublishSubject.create<String>()

    private val profilesFetchingTriggerObservable = PublishSubject.create<String>()

    private val questionFetchingTriggerObservable = PublishSubject.create<Boolean>()

    private val peopleView = object : PeopleView {

        override fun emitQuestionsFetchingTrigger(): Observable<Boolean> = questionFetchingTriggerObservable

        override fun emitPositiveMeet(): Observable<String> = positiveMeetObservable

        override fun emitNegativeMeet(): Observable<String> = negativeMeetObservable

        override fun emitProfilesFetchingTrigger(): Observable<String> = profilesFetchingTriggerObservable

        override fun render(peopleViewState: PeopleViewState) {
            renderedStates.add(peopleViewState)
        }
    }

    init {
        peopleViewModel.bind(peopleView, "testEventId")
    }

    fun triggerProfilesFetching() {
        profilesFetchingTriggerObservable.onNext("")
    }

    fun positiveAttendeeMeet(userId: String) {
        positiveMeetObservable.onNext(userId)
    }

    fun negativeAttendeeMeet(userId: String) {
        negativeMeetObservable.onNext(userId)
    }

    fun triggerQuestionsFetching() {
        questionFetchingTriggerObservable.onNext(true)
    }
}
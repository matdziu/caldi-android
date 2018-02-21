package com.caldi.addevent

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import junit.framework.Assert

class AddEventViewRobot(addEventViewModel: AddEventViewModel) {

    private val renderedStates = arrayListOf<AddEventViewState>()

    private val newEventCodeObservable: Subject<String> = PublishSubject.create()

    private val addEventView = object : AddEventView {

        override fun emitNewEventCode(): Observable<String> = newEventCodeObservable

        override fun render(addEventViewState: AddEventViewState) {
            renderedStates.add(addEventViewState)
        }
    }

    init {
        addEventViewModel.bind(addEventView)
    }

    fun emitAddNewEventButtonClick(eventCode: String) {
        newEventCodeObservable.onNext(eventCode)
    }

    fun assertViewStates(vararg expectedStates: AddEventViewState) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}
package com.caldi.addevent

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class AddEventViewRobot(addEventViewModel: AddEventViewModel) : BaseViewRobot<AddEventViewState>() {

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
}
package com.caldi.addevent

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class AddEventInteractor {

    fun addNewEvent(eventCode: String): Observable<PartialAddEventViewState> {
        val stateSubject: Subject<PartialAddEventViewState> = PublishSubject.create()
        return Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialAddEventViewState.ErrorState(true) }
                .startWith(PartialAddEventViewState.ErrorState()) as Observable<PartialAddEventViewState>
    }

    private fun emitError(stateSubject: Subject<PartialAddEventViewState>) {
        Observable.timer(100, TimeUnit.MILLISECONDS)
                .map { PartialAddEventViewState.ErrorState(true) }
                .startWith(PartialAddEventViewState.ErrorState())
                .subscribe(stateSubject)
    }
}
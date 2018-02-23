package com.caldi.eventprofile

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EventProfileViewModel(private val eventProfileInteractor: EventProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialEventProfileViewState>()

    fun bind(eventProfileView: EventProfileView) {
        val inputDataObservable = eventProfileView.emitInputData()
                .map { PartialEventProfileViewState.SuccessState() }

        val mergedObservable = Observable.merge(listOf(inputDataObservable)).subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.scan(EventProfileViewState(), this::reduce)
                .subscribe({ eventProfileView.render(it) }))
    }

    private fun reduce(previousState: EventProfileViewState, partialState: PartialEventProfileViewState)
            : EventProfileViewState {
        return when (partialState) {
            is PartialEventProfileViewState.SuccessState -> EventProfileViewState(true)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
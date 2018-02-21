package com.caldi.addevent

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class AddEventViewModel(private val addEventInteractor: AddEventInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject: Subject<PartialAddEventViewState> = BehaviorSubject.create()

    fun bind(addEventView: AddEventView) {
        val newEventCodeObservable = addEventView.emitNewEventCode()
                .flatMap { eventCode ->
                    addEventInteractor.addNewEvent(eventCode)
                            .startWith(PartialAddEventViewState.InProgressState())
                }
                .subscribeWith(stateSubject)

        compositeDisposable.add(newEventCodeObservable.scan(AddEventViewState(), this::reduce)
                .subscribe({ addEventView.render(it) }))
    }

    private fun reduce(previousState: AddEventViewState, partialState: PartialAddEventViewState)
            : AddEventViewState {
        return when (partialState) {
            is PartialAddEventViewState.ErrorState -> AddEventViewState(error = true, dismissToast = partialState.dismissToast)
            is PartialAddEventViewState.InProgressState -> AddEventViewState(inProgress = true)
            is PartialAddEventViewState.SuccessState -> AddEventViewState(success = true)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
package com.caldi.addevent

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class AddEventViewModel(private val addEventInteractor: AddEventInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject: Subject<AddEventViewState> = BehaviorSubject.create()

    fun bind(addEventView: AddEventView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
package com.caldi.eventprofile

import android.arch.lifecycle.ViewModel
import android.util.Log
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EventProfileViewModel(private val eventProfileInteractor: EventProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialEventProfileState>()

    fun bind(eventProfileView: EventProfileView) {
        val nameObservable = eventProfileView.emitInputData()

        nameObservable.subscribe({
            Log.d("mateusz", "name")
        })
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
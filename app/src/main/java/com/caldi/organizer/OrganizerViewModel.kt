package com.caldi.organizer

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class OrganizerViewModel(private val organizerInteractor: OrganizerInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<OrganizerViewState>()

    fun bind(organizerView: OrganizerView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
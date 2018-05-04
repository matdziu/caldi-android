package com.caldi.filterpeople

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class FilterPeopleViewModel(private val filterPeopleInteractor: FilterPeopleInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(FilterPeopleViewState())

    fun bind(filterPeopleView: FilterPeopleView) {


        val mergedObservable = Observable.merge(listOf(
                Observable.just(PartialFilterPeopleViewState.ProgressState())))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { filterPeopleView.render(it) })
    }

    private fun reduce(previousState: FilterPeopleViewState, partialState: PartialFilterPeopleViewState)
            : FilterPeopleViewState {
        return when (partialState) {
            is PartialFilterPeopleViewState.ProgressState -> previousState.copy(progress = true)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
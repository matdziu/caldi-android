package com.caldi.home

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject

class HomeViewModel(private val homeInteractor: HomeInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject: Subject<PartialHomeViewState> = BehaviorSubject.create()

    fun bind(homeView: HomeView) {
        val eventsFetchTriggerObservable = homeView.emitEventsFetchTrigger()
                .filter({ it })
                .flatMap { homeInteractor.fetchUserEvents().startWith(PartialHomeViewState.InProgressState()) }
                .subscribeWith(stateSubject)

        compositeDisposable.add(eventsFetchTriggerObservable.scan(HomeViewState(), this::reduce)
                .subscribe({ homeView.render(it) }))
    }

    private fun reduce(previousState: HomeViewState, partialState: PartialHomeViewState)
            : HomeViewState {
        return when (partialState) {
            is PartialHomeViewState.InProgressState -> HomeViewState(inProgress = true)
            is PartialHomeViewState.ErrorState -> HomeViewState(error = true,
                    dismissToast = partialState.dismissToast)
            is PartialHomeViewState.FetchingSucceeded -> HomeViewState(eventList = partialState.eventList)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
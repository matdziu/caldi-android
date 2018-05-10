package com.caldi.home

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class HomeViewModel(private val homeInteractor: HomeInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(HomeViewState())

    fun bind(homeView: HomeView) {
        val eventsFetchTriggerObservable = homeView.emitEventsFetchTrigger()
                .filter({ it })
                .doOnNext { homeInteractor.saveNotificationToken() }
                .flatMap { homeInteractor.fetchUserEvents().startWith(PartialHomeViewState.InProgressState()) }
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(eventsFetchTriggerObservable.subscribe({ homeView.render(it) }))
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
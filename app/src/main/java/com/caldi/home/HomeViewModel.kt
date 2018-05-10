package com.caldi.home

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class HomeViewModel(private val homeInteractor: HomeInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(HomeViewState())

    fun bind(homeView: HomeView) {
        val notificationTokenObservable = homeView.emitNotificationToken()
                .flatMap { homeInteractor.saveNotificationToken(it) }

        val eventsFetchTriggerObservable = homeView.emitEventsFetchTrigger()
                .flatMap { homeInteractor.fetchUserEvents().startWith(PartialHomeViewState.InProgressState()) }

        val mergedObservable = Observable.merge(listOf(
                notificationTokenObservable,
                eventsFetchTriggerObservable
        ))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.subscribe({ homeView.render(it) }))
    }

    private fun reduce(previousState: HomeViewState, partialState: PartialHomeViewState)
            : HomeViewState {
        return when (partialState) {
            is PartialHomeViewState.InProgressState -> HomeViewState(inProgress = true)
            is PartialHomeViewState.ErrorState -> HomeViewState(error = true,
                    dismissToast = partialState.dismissToast)
            is PartialHomeViewState.FetchingSucceeded -> HomeViewState(eventList = partialState.eventList)
            is PartialHomeViewState.NotificationTokenSaveSuccess -> previousState
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
package com.caldi.home

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import junit.framework.Assert

class HomeViewRobot(homeViewModel: HomeViewModel) {

    private val renderedStates = arrayListOf<HomeViewState>()

    private val eventsFetchTriggerObservable: Subject<Boolean> = PublishSubject.create()

    private val homeView = object : HomeView {

        override fun render(homeViewState: HomeViewState) {
            renderedStates.add(homeViewState)
        }

        override fun emitEventsFetchTrigger(): Observable<Boolean> = eventsFetchTriggerObservable
    }

    init {
        homeViewModel.bind(homeView)
    }

    fun emitEventsFetchTrigger() {
        eventsFetchTriggerObservable.onNext(true)
    }

    fun assertViewStates(vararg expectedStates: HomeViewState) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}
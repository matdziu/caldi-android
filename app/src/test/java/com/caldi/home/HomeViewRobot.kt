package com.caldi.home

import com.caldi.base.BaseViewRobot
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class HomeViewRobot(homeViewModel: HomeViewModel) : BaseViewRobot<HomeViewState>() {

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
}
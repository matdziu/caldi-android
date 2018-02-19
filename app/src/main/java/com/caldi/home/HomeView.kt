package com.caldi.home

import io.reactivex.Observable

interface HomeView {

    fun emitEventsFetchTrigger(): Observable<Boolean>

    fun render(homeViewState: HomeViewState)
}
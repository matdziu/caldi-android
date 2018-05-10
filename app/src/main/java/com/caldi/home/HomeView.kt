package com.caldi.home

import io.reactivex.Observable

interface HomeView {

    fun emitNotificationToken(): Observable<String>

    fun emitEventsFetchTrigger(): Observable<Boolean>

    fun render(homeViewState: HomeViewState)
}
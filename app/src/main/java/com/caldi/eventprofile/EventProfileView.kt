package com.caldi.eventprofile

import io.reactivex.Observable

interface EventProfileView {

    fun emitQuestionFetchingTrigger(): Observable<Boolean>

    fun render(eventProfileViewState: EventProfileViewState)
}
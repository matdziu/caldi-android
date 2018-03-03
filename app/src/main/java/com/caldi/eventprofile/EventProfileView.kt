package com.caldi.eventprofile

import io.reactivex.Observable

interface EventProfileView {

    fun emitQuestionFetchingTrigger(): Observable<String>

    fun render(eventProfileViewState: EventProfileViewState)
}
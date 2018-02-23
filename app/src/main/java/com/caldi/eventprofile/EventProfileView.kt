package com.caldi.eventprofile

import io.reactivex.Observable

interface EventProfileView {

    fun emitInputData(): Observable<InputData>

    fun render(eventProfileState: EventProfileState)
}
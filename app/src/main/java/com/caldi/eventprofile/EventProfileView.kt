package com.caldi.eventprofile

import com.caldi.eventprofile.models.EventProfileData
import io.reactivex.Observable

interface EventProfileView {

    fun emitEventProfileFetchingTrigger(): Observable<String>

    fun emitInputData(): Observable<Pair<String, EventProfileData>>

    fun emitUserNameInput(): Observable<String>

    fun render(eventProfileViewState: EventProfileViewState)
}
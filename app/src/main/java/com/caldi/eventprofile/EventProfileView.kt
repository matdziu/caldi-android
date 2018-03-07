package com.caldi.eventprofile

import com.caldi.eventprofile.models.EventProfileData
import io.reactivex.Observable
import java.io.File

interface EventProfileView {

    fun emitEventProfileFetchingTrigger(): Observable<String>

    fun emitInputData(): Observable<EventProfileData>

    fun emitProfilePictureFile(): Observable<File>

    fun render(eventProfileViewState: EventProfileViewState)
}
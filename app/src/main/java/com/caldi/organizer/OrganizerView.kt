package com.caldi.organizer

import io.reactivex.Observable

interface OrganizerView {

    fun emitNewMessagesListeningToggle(): Observable<Boolean>

    fun emitEventInfoFetchTrigger(): Observable<Boolean>

    fun emitBatchFetchTrigger(): Observable<String>

    fun render(organizerViewState: OrganizerViewState)
}
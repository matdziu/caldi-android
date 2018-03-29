package com.caldi.organizer

import io.reactivex.Observable

interface OrganizerView {

    fun emitBatchFetchTrigger(): Observable<String>

    fun render(organizerViewState: OrganizerViewState)
}
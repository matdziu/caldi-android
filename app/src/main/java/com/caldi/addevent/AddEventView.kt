package com.caldi.addevent

import io.reactivex.Observable

interface AddEventView {

    fun emitNewEventCode(): Observable<String>

    fun render(addEventViewState: AddEventViewState)
}
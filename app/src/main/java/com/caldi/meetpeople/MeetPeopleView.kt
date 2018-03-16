package com.caldi.meetpeople

import io.reactivex.Observable

interface MeetPeopleView {

    fun emitProfilesFetchingTrigger(): Observable<String>

    fun render(meetPeopleViewState: MeetPeopleViewState)
}
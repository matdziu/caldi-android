package com.caldi.meetpeople

import io.reactivex.Observable

interface MeetPeopleView {

    fun emitPositiveMeet(): Observable<String>

    fun emitNegativeMeet(): Observable<String>

    fun emitProfilesFetchingTrigger(): Observable<String>

    fun render(meetPeopleViewState: MeetPeopleViewState)
}
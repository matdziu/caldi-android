package com.caldi.people.common

import io.reactivex.Observable

interface PeopleView {

    fun emitPositiveMeet(): Observable<String>

    fun emitNegativeMeet(): Observable<String>

    fun emitProfilesFetchingTrigger(): Observable<Boolean>

    fun render(peopleViewState: PeopleViewState)
}
package com.caldi.people.common

import io.reactivex.Observable

interface PeopleView {

    fun emitPositiveMeet(): Observable<String>

    fun emitNegativeMeet(): Observable<String>

    fun emitQuestionsFetchingTrigger(): Observable<Boolean>

    fun emitProfilesFetchingTrigger(): Observable<String>

    fun render(peopleViewState: PeopleViewState)
}
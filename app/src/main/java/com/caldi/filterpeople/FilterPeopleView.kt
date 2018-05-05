package com.caldi.filterpeople

import io.reactivex.Observable

interface FilterPeopleView {

    fun emitProfilesFetchingTrigger(): Observable<Boolean>

    fun render(filterPeopleViewState: FilterPeopleViewState)
}
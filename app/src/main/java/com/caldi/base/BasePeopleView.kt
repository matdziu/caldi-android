package com.caldi.base

import io.reactivex.Observable

interface BasePeopleView {

    fun emitPositiveMeet(): Observable<String>

    fun emitNegativeMeet(): Observable<String>

    fun emitProfilesFetchingTrigger(): Observable<Boolean>
}
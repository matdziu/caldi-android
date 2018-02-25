package com.caldi.eventprofile.list

import io.reactivex.Observable

interface QuestionItemView {

    fun defaultRender(questionViewState: QuestionViewState)

    fun emitUserInput(): Observable<String>
}
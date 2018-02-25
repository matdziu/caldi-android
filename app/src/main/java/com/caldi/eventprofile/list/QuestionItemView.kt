package com.caldi.eventprofile.list

import io.reactivex.Observable

interface QuestionItemView {

    fun render(questionViewState: QuestionViewState)

    fun emitUserInput(): Observable<String>
}
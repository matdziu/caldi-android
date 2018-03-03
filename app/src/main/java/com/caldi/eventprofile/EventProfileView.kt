package com.caldi.eventprofile

import com.caldi.eventprofile.models.Answer
import io.reactivex.Observable

interface EventProfileView {

    fun emitQuestionFetchingTrigger(): Observable<String>

    fun emitAnswers(): Observable<Pair<String, List<Answer>>>

    fun render(eventProfileViewState: EventProfileViewState)
}
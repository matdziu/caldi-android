package com.caldi.eventprofile.list

import com.caldi.eventprofile.models.Answer
import io.reactivex.Observable

interface QuestionItemView {

    fun emitAnswer(): Observable<Answer>
}
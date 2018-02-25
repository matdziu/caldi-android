package com.caldi.eventprofile

import com.caldi.eventprofile.models.Question
import io.reactivex.Observable

class EventProfileInteractor {

    fun fetchQuestions(): Observable<PartialEventProfileViewState> {
        return Observable.just(PartialEventProfileViewState.SuccessState(listOf(Question("1", "1"),
                Question("2", "2"),
                Question("3", "3"),
                Question("4", "4"),
                Question("5", "5"))))
    }
}
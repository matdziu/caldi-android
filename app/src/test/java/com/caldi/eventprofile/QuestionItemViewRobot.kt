package com.caldi.eventprofile

import com.caldi.base.BaseViewRobot
import com.caldi.eventprofile.list.QuestionViewState
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class QuestionItemViewRobot(private val questionsViewModel: QuestionsViewModel) : BaseViewRobot<QuestionViewState>() {

    private val userInputObservable = PublishSubject.create<String>()

    private val questionItemView = object {

        override fun bind(questionViewState: QuestionViewState) {
            renderedStates.add(questionViewState)
        }

        override fun emitUserInput(): Observable<String> = userInputObservable
    }

    fun init(defaultState: QuestionViewState) {
        questionsViewModel.setQuestionItemStateList(listOf(defaultState))
        questionsViewModel.bind(questionItemView, 0)
    }

    fun emitUserInput(input: String) {
        userInputObservable.onNext(input)
    }
}
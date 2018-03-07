package com.caldi.eventprofile

import com.caldi.eventprofile.list.QuestionItemView
import com.caldi.eventprofile.list.QuestionViewState
import com.caldi.eventprofile.list.QuestionsViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import junit.framework.Assert

class QuestionItemViewRobot(private val questionsViewModel: QuestionsViewModel) {

    private val renderedStates = arrayListOf<QuestionViewState>()

    private val userInputObservable = PublishSubject.create<String>()

    private val questionItemView = object : QuestionItemView {

        override fun defaultRender(questionViewState: QuestionViewState) {
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

    fun assertViewStates(vararg expectedStates: QuestionViewState) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}
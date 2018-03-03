package com.caldi.eventprofile

import android.arch.lifecycle.ViewModel
import com.caldi.eventprofile.list.QuestionViewState
import com.caldi.eventprofile.models.Question
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EventProfileViewModel(private val eventProfileInteractor: EventProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialEventProfileViewState>()

    fun bind(eventProfileView: EventProfileView) {
        val fetchQuestionsObservable = eventProfileView.emitQuestionFetchingTrigger()
                .flatMap { eventProfileInteractor.fetchQuestions(it).startWith(PartialEventProfileViewState.ProgressState()) }

        val mergedObservable = Observable.merge(listOf(fetchQuestionsObservable)).subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.scan(EventProfileViewState(), this::reduce)
                .subscribe({ eventProfileView.render(it) }))
    }

    private fun reduce(previousState: EventProfileViewState, partialState: PartialEventProfileViewState)
            : EventProfileViewState {
        return when (partialState) {
            is PartialEventProfileViewState.ProgressState -> EventProfileViewState(progress = true)
            is PartialEventProfileViewState.ErrorState -> EventProfileViewState(error = true)
            is PartialEventProfileViewState.SuccessState -> EventProfileViewState(success = true,
                    questionViewStateList = convertToQuestionViewStateList(partialState.questionsList))
        }
    }

    private fun convertToQuestionViewStateList(questionList: List<Question>): List<QuestionViewState> {
        return questionList.map { QuestionViewState(questionText = it.text, questionId = it.id) }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
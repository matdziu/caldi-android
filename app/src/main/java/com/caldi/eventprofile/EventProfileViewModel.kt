package com.caldi.eventprofile

import android.arch.lifecycle.ViewModel
import com.caldi.eventprofile.list.QuestionViewState
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.Question
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EventProfileViewModel(private val eventProfileInteractor: EventProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialEventProfileViewState>()

    fun bind(eventProfileView: EventProfileView) {
        val fetchQuestionsObservable = eventProfileView.emitQuestionFetchingTrigger()
                .flatMap { eventProfileInteractor.fetchEventProfile(it).startWith(PartialEventProfileViewState.ProgressState()) }

        val updateAnswersObservable = eventProfileView.emitInputData()
                .flatMap {
                    eventProfileInteractor.updateEventProfile(it.first, it.second)
                            .startWith(PartialEventProfileViewState.ProgressState())
                }

        val mergedObservable = Observable.merge(listOf(fetchQuestionsObservable,
                updateAnswersObservable)).subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.scan(EventProfileViewState(), this::reduce)
                .subscribe({ eventProfileView.render(it) }))
    }

    private fun reduce(previousState: EventProfileViewState, partialState: PartialEventProfileViewState)
            : EventProfileViewState {
        return when (partialState) {
            is PartialEventProfileViewState.ProgressState -> EventProfileViewState(progress = true)
            is PartialEventProfileViewState.ErrorState -> EventProfileViewState(error = true)
            is PartialEventProfileViewState.SuccessfulFetchState -> EventProfileViewState(successFetch = true,
                    eventUserName = partialState.eventProfileData.eventUserName,
                    questionViewStateList = convertToQuestionViewStateList(partialState.eventProfileData.questionList,
                            partialState.eventProfileData.answerList))
            is PartialEventProfileViewState.SuccessfulUpdateState ->
                previousState.copy(progress = false, error = false, successUpload = true, dismissToast = partialState.dismissToast)
        }
    }

    private fun convertToQuestionViewStateList(questionList: List<Question>, answerList: List<Answer>)
            : List<QuestionViewState> {
        val answersMap = answerList.map { it.questionId to it.answer }.toMap()
        return questionList.map {
            QuestionViewState(it.question, answersMap.getOrDefault(it.id, ""), it.id)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
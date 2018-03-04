package com.caldi.eventprofile

import android.arch.lifecycle.ViewModel
import com.caldi.eventprofile.list.QuestionViewState
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.Question
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EventProfileViewModel(private val eventProfileInteractor: EventProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialEventProfileViewState>()

    fun bind(eventProfileView: EventProfileView) {
        val fetchEventProfileObservable = eventProfileView.emitEventProfileFetchingTrigger()
                .flatMap {
                    eventProfileInteractor.fetchEventProfile(it)
                            .startWith(PartialEventProfileViewState.ProgressState())
                }

        val updateProfileObservable = eventProfileView.emitInputData()
                .flatMap {
                    val eventId = it.first
                    val eventProfileData = it.second

                    val eventUserNameValid = !eventProfileData.eventUserName.isBlank()
                    var eachAnswerValid = true

                    for (answer in eventProfileData.answerList) {
                        answer.valid = !answer.answer.isBlank()
                        if (!answer.valid) eachAnswerValid = false
                    }

                    if (!eventUserNameValid || !eachAnswerValid) {
                        Observable.just(PartialEventProfileViewState.LocalValidation(eventUserNameValid,
                                eventProfileData.answerList, eventProfileData.questionList))
                    } else {
                        eventProfileInteractor.updateEventProfile(eventId, eventProfileData)
                                .startWith(PartialEventProfileViewState.ProgressState())
                    }
                }

        val mergedObservable = Observable.merge(listOf(fetchEventProfileObservable,
                updateProfileObservable)).subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.scan(EventProfileViewState(), this::reduce)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ eventProfileView.render(it) }))
    }

    private fun reduce(previousState: EventProfileViewState, partialState: PartialEventProfileViewState)
            : EventProfileViewState {
        return when (partialState) {
            is PartialEventProfileViewState.ProgressState -> EventProfileViewState(progress = true)
            is PartialEventProfileViewState.ErrorState -> EventProfileViewState(error = true)
            is PartialEventProfileViewState.SuccessfulFetchState ->
                EventProfileViewState(
                        eventUserName = partialState.eventProfileData.eventUserName,
                        questionViewStateList = convertToQuestionViewStateList(partialState.eventProfileData.questionList,
                                partialState.eventProfileData.answerList),
                        renderEventName = partialState.renderEventName)
            is PartialEventProfileViewState.SuccessfulUpdateState ->
                previousState.copy(
                        progress = false,
                        error = false,
                        successUpload = true,
                        dismissToast = partialState.dismissToast)
            is PartialEventProfileViewState.LocalValidation ->
                previousState.copy(
                        eventUserNameValid = partialState.eventUserNameValid,
                        questionViewStateList = convertToQuestionViewStateList(partialState.questionList,
                                partialState.answerList))
        }
    }

    private fun convertToQuestionViewStateList(questionList: List<Question>, answerList: List<Answer>)
            : List<QuestionViewState> {
        val answersMap = answerList.map { it.questionId to it }.toMap()
        return questionList.map {
            val currentAnswer = answersMap.getOrDefault(it.id, Answer())
            QuestionViewState(it.question, currentAnswer.answer, it.id, currentAnswer.valid)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
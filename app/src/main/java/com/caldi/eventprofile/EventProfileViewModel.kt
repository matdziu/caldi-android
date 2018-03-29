package com.caldi.eventprofile

import android.arch.lifecycle.ViewModel
import com.caldi.common.models.Answer
import com.caldi.common.models.Question
import com.caldi.eventprofile.list.QuestionViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EventProfileViewModel(private val eventProfileInteractor: EventProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialEventProfileViewState>()
    private var eventId: String = ""

    fun bind(eventProfileView: EventProfileView) {
        val fetchEventProfileObservable = eventProfileView.emitEventProfileFetchingTrigger()
                .flatMap {
                    eventId = it
                    eventProfileInteractor.fetchEventProfile(it)
                            .startWith(PartialEventProfileViewState.ProgressState())
                }

        val updateProfileObservable = eventProfileView.emitInputData()
                .flatMap {
                    val eventUserNameValid = !it.eventUserName.isBlank()
                    var eachAnswerValid = true

                    for (answer in it.answerList) {
                        answer.valid = !answer.answer.isBlank()
                        if (!answer.valid) eachAnswerValid = false
                    }

                    if (!eventUserNameValid || !eachAnswerValid) {
                        Observable.just(PartialEventProfileViewState.LocalValidation(it.eventUserName,
                                eventUserNameValid, it.answerList, it.questionList))
                    } else {
                        eventProfileInteractor.updateEventProfile(eventId, it)
                                .startWith(PartialEventProfileViewState.ProgressState())
                    }
                }

        val profilePictureFileObservable = eventProfileView.emitProfilePictureFile()
                .flatMap {
                    eventProfileInteractor.uploadProfilePicture(eventId, it)
                            .startWith(PartialEventProfileViewState.ProgressState())
                }

        val mergedObservable = Observable.merge(listOf(fetchEventProfileObservable,
                updateProfileObservable, profilePictureFileObservable)).subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.scan(EventProfileViewState(), this::reduce)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ eventProfileView.render(it) }))
    }

    private fun reduce(previousState: EventProfileViewState, partialState: PartialEventProfileViewState)
            : EventProfileViewState {
        return when (partialState) {
            is PartialEventProfileViewState.ProgressState -> EventProfileViewState(progress = true)
            is PartialEventProfileViewState.ErrorState -> EventProfileViewState(error = true, dismissToast = partialState.dismissToast)
            is PartialEventProfileViewState.SuccessfulFetchState ->
                EventProfileViewState(
                        eventUserName = partialState.eventProfileData.eventUserName,
                        questionViewStateList = convertToQuestionViewStateList(partialState.eventProfileData.questionList,
                                partialState.eventProfileData.answerList),
                        renderEventName = partialState.renderEventName,
                        profilePictureUrl = partialState.eventProfileData.profilePictureUrl)
            is PartialEventProfileViewState.SuccessfulUpdateState ->
                previousState.copy(
                        progress = false,
                        error = false,
                        successUpload = true,
                        dismissToast = partialState.dismissToast)
            is PartialEventProfileViewState.LocalValidation ->
                previousState.copy(
                        eventUserName = partialState.eventUserName,
                        eventUserNameValid = partialState.eventUserNameValid,
                        questionViewStateList = convertToQuestionViewStateList(partialState.questionList,
                                partialState.answerList))
            is PartialEventProfileViewState.SuccessfulPictureUploadState ->
                previousState.copy(
                        progress = false,
                        profilePictureUrl = partialState.pictureUrl
                )
        }
    }

    private fun convertToQuestionViewStateList(questionList: List<Question>, answerList: List<Answer>)
            : List<QuestionViewState> {
        val answersMap = answerList.map { it.questionId to it }.toMap()
        return questionList.map {
            val currentAnswer = answersMap[it.id] ?: Answer()
            QuestionViewState(it.question, currentAnswer.answer, it.id, currentAnswer.valid)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
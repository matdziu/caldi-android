package com.caldi.eventprofile

import android.arch.lifecycle.ViewModel
import com.caldi.common.models.EventProfileData
import com.caldi.eventprofile.list.QuestionViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class EventProfileViewModel(private val eventProfileInteractor: EventProfileInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(EventProfileViewState())
    private var eventId: String = ""

    fun bind(eventProfileView: EventProfileView) {
        val fetchEventProfileObservable = eventProfileView.emitEventProfileFetchingTrigger()
                .flatMap {
                    eventId = it
                    eventProfileInteractor.fetchEventProfile(it)
                            .startWith(PartialEventProfileViewState.ProgressState())
                }

        val updateProfileObservable = eventProfileView.emitEventProfileData()
                .flatMap {
                    val eventUserNameValid = it.eventUserName.isNotBlank()
                    val answerValidMap = hashMapOf<String, Boolean>()
                    var eachAnswerValid = true

                    for ((questionId, answer) in it.answers) {
                        val isAnswerValid = answer.isNotBlank()
                        answerValidMap[questionId] = isAnswerValid
                        if (!isAnswerValid) eachAnswerValid = false
                    }

                    val localValidationState = PartialEventProfileViewState.LocalValidation(
                            eventProfileData = it,
                            eventUserNameValid = eventUserNameValid,
                            answerValidMap = answerValidMap,
                            renderInputs = false
                    )

                    if (!eventUserNameValid || !eachAnswerValid) {
                        Observable.just(localValidationState)
                                .startWith(localValidationState.copy(renderInputs = true))
                    } else {
                        Observable.concat(
                                Observable.just(localValidationState),
                                eventProfileInteractor.updateEventProfile(eventId, it)
                                        .startWith(PartialEventProfileViewState.ProgressState())
                        )
                    }
                }

        val profilePictureFileObservable = eventProfileView.emitProfilePictureFile()
                .flatMap {
                    eventProfileInteractor.uploadProfilePicture(eventId, it)
                            .startWith(PartialEventProfileViewState.ProgressState())
                }

        val mergedObservable = Observable.merge(listOf(
                fetchEventProfileObservable,
                updateProfileObservable,
                profilePictureFileObservable))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ eventProfileView.render(it) }))
    }

    private fun reduce(previousState: EventProfileViewState, partialState: PartialEventProfileViewState)
            : EventProfileViewState {
        return when (partialState) {
            is PartialEventProfileViewState.ProgressState ->
                previousState.copy(
                        progress = true)
            is PartialEventProfileViewState.ErrorState ->
                previousState.copy(
                        progress = false,
                        error = true,
                        dismissToast = partialState.dismissToast)
            is PartialEventProfileViewState.SuccessfulFetchState ->
                EventProfileViewState(
                        eventUserName = partialState.eventProfileData.eventUserName,
                        profilePictureUrl = partialState.eventProfileData.profilePictureUrl,
                        userLinkUrl = partialState.eventProfileData.userLinkUrl,
                        questionViewStates = convertToQuestionViewStates(partialState.questions, partialState.eventProfileData.answers),
                        renderInputs = partialState.renderInputs)
            is PartialEventProfileViewState.SuccessfulUpdateState ->
                previousState.copy(
                        progress = false,
                        error = false,
                        updateSuccess = true,
                        dismissToast = partialState.dismissToast)
            is PartialEventProfileViewState.LocalValidation ->
                previousState.copy(
                        progress = false,
                        eventUserName = partialState.eventProfileData.eventUserName,
                        userLinkUrl = partialState.eventProfileData.userLinkUrl,
                        profilePictureUrl = partialState.eventProfileData.profilePictureUrl,
                        questionViewStates = applyValidationToQuestionViewStates(
                                previousState.questionViewStates,
                                partialState.eventProfileData,
                                partialState.answerValidMap),
                        eventUserNameValid = partialState.eventUserNameValid,
                        renderInputs = partialState.renderInputs)
            is PartialEventProfileViewState.SuccessfulPictureUploadState ->
                previousState.copy(
                        profilePictureUrl = partialState.profilePictureUrl,
                        progress = false
                )
        }
    }

    private fun convertToQuestionViewStates(questions: Map<String, String>,
                                            answers: Map<String, String>): List<QuestionViewState> {
        val questionViewStates = arrayListOf<QuestionViewState>()
        for ((questionId, questionText) in questions) {
            questionViewStates.add(QuestionViewState(questionText,
                    answers[questionId] ?: "", questionId))
        }
        return questionViewStates
    }

    private fun applyValidationToQuestionViewStates(questionViewStates: List<QuestionViewState>,
                                                    eventProfileData: EventProfileData,
                                                    answerValidMap: Map<String, Boolean>): List<QuestionViewState> {
        val validatedQuestionViewStates = arrayListOf<QuestionViewState>()
        for (questionViewState in questionViewStates) {
            validatedQuestionViewStates.add(questionViewState.copy(
                    answerText = eventProfileData.answers[questionViewState.questionId] ?: "",
                    answerValid = answerValidMap[questionViewState.questionId] ?: false))
        }
        return validatedQuestionViewStates
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
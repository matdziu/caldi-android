package com.caldi.people.common

import android.arch.lifecycle.ViewModel
import com.caldi.common.models.EventProfileData
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.meetpeople.list.AnswerViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class PeopleViewModel(private val peopleInteractor: PeopleInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(PeopleViewState())

    private var currentEventQuestions = mapOf<String, String>()

    fun bind(peopleView: PeopleView, eventId: String) {
        val fetchProfilesObservable = peopleView.emitProfilesFetchingTrigger()
                .flatMap {
                    peopleInteractor.fetchAttendeesProfiles(eventId, it)
                            .startWith(PartialPeopleViewState.ProgressState())
                }

        val positiveMeetObservable = peopleView.emitPositiveMeet()
                .flatMap { peopleInteractor.saveMetAttendee(it, eventId, PeopleInteractor.MeetType.POSITIVE) }

        val negativeMeetObservable = peopleView.emitNegativeMeet()
                .flatMap { peopleInteractor.saveMetAttendee(it, eventId, PeopleInteractor.MeetType.NEGATIVE) }

        val fetchQuestionsObservable = peopleView.emitQuestionsFetchingTrigger()
                .flatMap { peopleInteractor.fetchQuestions(eventId) }
                .map { PartialPeopleViewState.SuccessfulQuestionsFetchState(it) }
                .doOnNext { currentEventQuestions = it.questions }

        val mergedObservable = Observable.merge(listOf(
                fetchProfilesObservable,
                positiveMeetObservable,
                negativeMeetObservable,
                fetchQuestionsObservable,
                peopleInteractor.checkIfEventProfileIsFilled(eventId)))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { peopleView.render(it) })
    }

    private fun reduce(previousState: PeopleViewState, partialState: PartialPeopleViewState):
            PeopleViewState {
        return when (partialState) {
            is PartialPeopleViewState.ProgressState -> previousState.copy(
                    progress = true)
            is PartialPeopleViewState.ErrorState -> PeopleViewState(
                    error = true,
                    dismissToast = partialState.dismissToast)
            is PartialPeopleViewState.SuccessfulAttendeesFetchState -> previousState.copy(
                    progress = false,
                    error = false,
                    personProfileViewStateList = convertToPersonProfileViewStateList(partialState.attendeesProfilesList))
            is PartialPeopleViewState.SuccessfulMetAttendeeSave -> previousState
            is PartialPeopleViewState.BlankEventProfileState -> PeopleViewState(
                    eventProfileBlank = true,
                    dismissToast = partialState.dismissToast)
            is PartialPeopleViewState.SuccessfulQuestionsFetchState -> previousState.copy(
                    eventQuestions = partialState.questions)
        }
    }

    private fun convertToPersonProfileViewStateList(attendeesProfilesList: List<EventProfileData>)
            : List<PersonProfileViewState> {
        return attendeesProfilesList.map {
            PersonProfileViewState(
                    it.userId,
                    it.eventUserName,
                    it.profilePicture,
                    it.userLinkUrl,
                    convertToAnswerViewStateList(currentEventQuestions, it.answers))
        }.reversed()
    }

    private fun convertToAnswerViewStateList(questions: Map<String, String>, answers: Map<String, String>)
            : List<AnswerViewState> {
        val answerViewStateList = arrayListOf<AnswerViewState>()
        for ((questionId, question) in questions) {
            answerViewStateList.add(AnswerViewState(question, answers[questionId] ?: ""))
        }
        return answerViewStateList
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
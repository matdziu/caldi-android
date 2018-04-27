package com.caldi.meetpeople

import android.arch.lifecycle.ViewModel
import com.caldi.meetpeople.list.AnswerViewState
import com.caldi.meetpeople.models.AttendeeProfile
import com.caldi.meetpeople.personprofile.PersonProfileViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MeetPeopleViewModel(private val meetPeopleInteractor: MeetPeopleInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(MeetPeopleViewState())

    fun bind(meetPeopleView: MeetPeopleView, eventId: String) {
        val fetchProfilesObservable = meetPeopleView.emitProfilesFetchingTrigger()
                .flatMap {
                    meetPeopleInteractor.fetchAttendeesProfiles(eventId)
                            .startWith(PartialMeetPeopleViewState.ProgressState())
                }

        val positiveMeetObservable = meetPeopleView.emitPositiveMeet()
                .flatMap { meetPeopleInteractor.saveMetAttendee(it, eventId, MeetPeopleInteractor.MeetType.POSITIVE) }

        val negativeMeetObservable = meetPeopleView.emitNegativeMeet()
                .flatMap { meetPeopleInteractor.saveMetAttendee(it, eventId, MeetPeopleInteractor.MeetType.NEGATIVE) }

        val mergedObservable = Observable.merge(listOf(
                fetchProfilesObservable,
                positiveMeetObservable,
                negativeMeetObservable,
                meetPeopleInteractor.checkIfEventProfileIsFilled(eventId)))
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { meetPeopleView.render(it) })
    }

    private fun reduce(previousState: MeetPeopleViewState, partialState: PartialMeetPeopleViewState):
            MeetPeopleViewState {
        return when (partialState) {
            is PartialMeetPeopleViewState.ProgressState -> MeetPeopleViewState(
                    progress = true)
            is PartialMeetPeopleViewState.ErrorState -> MeetPeopleViewState(
                    error = true,
                    dismissToast = partialState.dismissToast)
            is PartialMeetPeopleViewState.SuccessfulAttendeesFetchState -> MeetPeopleViewState(
                    personProfileViewStateList = convertToPersonProfileViewStateList(partialState.attendeesProfilesList))
            is PartialMeetPeopleViewState.SuccessfulMetAttendeeSave -> previousState
            is PartialMeetPeopleViewState.BlankEventProfileState -> MeetPeopleViewState(
                    eventProfileBlank = true,
                    dismissToast = partialState.dismissToast)
        }
    }

    private fun convertToPersonProfileViewStateList(attendeesProfilesList: List<AttendeeProfile>)
            : List<PersonProfileViewState> {
        return attendeesProfilesList.map {
            PersonProfileViewState(
                    it.userId,
                    it.eventUserName,
                    it.profilePictureUrl,
                    it.userLinkUrl,
                    convertToAnswerViewStateList(it.questions, it.answers))
        }
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
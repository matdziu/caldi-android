package com.caldi.meetpeople

import android.arch.lifecycle.ViewModel
import com.caldi.base.models.Answer
import com.caldi.base.models.Question
import com.caldi.meetpeople.list.AnswerViewState
import com.caldi.meetpeople.models.AttendeeProfile
import com.caldi.meetpeople.personprofile.PersonProfileViewState
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class MeetPeopleViewModel(private val meetPeopleInteractor: MeetPeopleInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialMeetPeopleViewState>()
    private var eventId: String = ""

    fun bind(meetPeopleView: MeetPeopleView) {
        val fetchProfilesObservable = meetPeopleView.emitProfilesFetchingTrigger()
                .flatMap {
                    eventId = it
                    meetPeopleInteractor.fetchAttendeesProfiles(it)
                            .startWith(PartialMeetPeopleViewState.ProgressState())
                }

        val positiveMeetObservable = meetPeopleView.emitPositiveMeet()
                .flatMap { meetPeopleInteractor.saveMetAttendee(it, eventId, MeetPeopleInteractor.MeetType.POSITIVE) }

        val negativeMeetObservable = meetPeopleView.emitNegativeMeet()
                .flatMap { meetPeopleInteractor.saveMetAttendee(it, eventId, MeetPeopleInteractor.MeetType.NEGATIVE) }

        val mergedObservable = Observable.merge(listOf(fetchProfilesObservable,
                positiveMeetObservable, negativeMeetObservable))
                .subscribeWith(stateSubject)

        compositeDisposable.add(mergedObservable.scan(MeetPeopleViewState(), this::reduce)
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
        }
    }

    private fun convertToPersonProfileViewStateList(attendeesProfilesList: List<AttendeeProfile>)
            : List<PersonProfileViewState> {
        return attendeesProfilesList.map {
            PersonProfileViewState(
                    it.userId,
                    it.eventUserName,
                    it.profilePictureUrl,
                    convertToAnswerViewStateList(it.questionList, it.answerList))
        }
    }

    private fun convertToAnswerViewStateList(questionList: List<Question>, answerList: List<Answer>)
            : List<AnswerViewState> {
        val answersMap = answerList.map { it.questionId to it.answer }.toMap()
        return questionList.map {
            val currentAnswer = answersMap[it.id] ?: ""
            AnswerViewState(it.question, currentAnswer)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
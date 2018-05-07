package com.caldi.people

import com.caldi.common.models.EventProfileData
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.common.PartialPeopleViewState
import com.caldi.people.common.PeopleInteractor
import com.caldi.people.common.PeopleViewModel
import com.caldi.people.common.PeopleViewState
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class PeopleViewModelTest {

    private val peopleInteractor: PeopleInteractor = mock()
    private val peopleViewModel: PeopleViewModel = PeopleViewModel(peopleInteractor)

    init {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testSuccessfulProfilesFetching() {
        val answers = mapOf("answerId" to "Sample answer")
        val attendeesList = listOf(EventProfileData(
                "123",
                "Matt",
                answers,
                "url/to/pic",
                "user/url"))
        whenever(peopleInteractor.fetchAttendeesProfiles(any())).thenReturn(
                Observable.just(PartialPeopleViewState.SuccessfulAttendeesFetchState(attendeesList)))
        whenever(peopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        val peopleViewRobot = PeopleViewRobot(peopleViewModel)

        peopleViewRobot.triggerProfilesFetching()

        peopleViewRobot.assertViewStates(
                PeopleViewState(),
                PeopleViewState(progress = true),
                PeopleViewState(personProfileViewStateList = listOf(PersonProfileViewState(
                        "123", "Matt", "url/to/pic",
                        "user/url")))
        )
    }

    @Test
    fun testNoProfilesFetching() {
        whenever(peopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Observable.just(PartialPeopleViewState.BlankEventProfileState())
        )
        val peopleViewRobot = PeopleViewRobot(peopleViewModel)

        peopleViewRobot.assertViewStates(
                PeopleViewState(eventProfileBlank = true)
        )
    }

    @Test
    fun testSuccessfulPositiveMeetAttendeeSave() {
        whenever(peopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        whenever(peopleInteractor.saveMetAttendee(any(), any(), eq(PeopleInteractor.MeetType.POSITIVE)))
                .thenReturn(Observable.just(PartialPeopleViewState.SuccessfulMetAttendeeSave()))
        val peopleViewRobot = PeopleViewRobot(peopleViewModel)

        peopleViewRobot.positiveAttendeeMeet("123")

        peopleViewRobot.assertViewStates(
                PeopleViewState(),
                PeopleViewState()
        )
    }

    @Test
    fun testSuccessfulNegativeMeetAttendeeSave() {
        whenever(peopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        whenever(peopleInteractor.saveMetAttendee(any(), any(), eq(PeopleInteractor.MeetType.NEGATIVE)))
                .thenReturn(Observable.just(PartialPeopleViewState.SuccessfulMetAttendeeSave()))
        val peopleViewRobot = PeopleViewRobot(peopleViewModel)

        peopleViewRobot.negativeAttendeeMeet("123")

        peopleViewRobot.assertViewStates(
                PeopleViewState(),
                PeopleViewState()
        )
    }

    @Test
    fun testSuccessfulQuestionsFetching() {
        whenever(peopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        val questions = mapOf(
                "firstQuestionId" to "What's your favourite drink?",
                "secondQuestionId" to "What's your favourite song?"
        )
        whenever(peopleInteractor.fetchQuestions(any())).thenReturn(Observable.just(questions))
        val peopleViewRobot = PeopleViewRobot(peopleViewModel)

        peopleViewRobot.triggerQuestionsFetching()

        peopleViewRobot.assertViewStates(
                PeopleViewState(),
                PeopleViewState(eventQuestions = questions)
        )
    }
}
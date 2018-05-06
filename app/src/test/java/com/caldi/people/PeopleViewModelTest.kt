package com.caldi.people

import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.common.PartialPeopleViewState
import com.caldi.people.common.PeopleInteractor
import com.caldi.people.common.PeopleViewModel
import com.caldi.people.common.PeopleViewState
import com.caldi.people.common.models.AttendeeProfile
import com.caldi.people.meetpeople.list.AnswerViewState
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
        val questions = mapOf("1" to "What is your favourite color?")
        val answers = mapOf("1" to "Red")
        val attendeesList = listOf(AttendeeProfile(
                "123",
                "Matt",
                "user/url",
                "url/to/pic",
                answers,
                questions))
        whenever(peopleInteractor.fetchAttendeesProfiles(any())).thenReturn(
                Observable.just(PartialPeopleViewState.SuccessfulAttendeesFetchState(attendeesList)))
        whenever(peopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        val meetPeopleViewRobot = PeopleViewRobot(peopleViewModel)

        meetPeopleViewRobot.triggerProfilesFetching()

        meetPeopleViewRobot.assertViewStates(
                PeopleViewState(),
                PeopleViewState(progress = true),
                PeopleViewState(personProfileViewStateList = listOf(PersonProfileViewState(
                        "123", "Matt", "url/to/pic",
                        "user/url",
                        listOf(AnswerViewState("What is your favourite color?", "Red")))))
        )
    }

    @Test
    fun testNoProfilesFetching() {
        whenever(peopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Observable.just(PartialPeopleViewState.BlankEventProfileState())
        )
        val meetPeopleViewRobot = PeopleViewRobot(peopleViewModel)

        meetPeopleViewRobot.assertViewStates(
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
        val meetPeopleViewRobot = PeopleViewRobot(peopleViewModel)

        meetPeopleViewRobot.positiveAttendeeMeet("123")

        meetPeopleViewRobot.assertViewStates(
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
        val meetPeopleViewRobot = PeopleViewRobot(peopleViewModel)

        meetPeopleViewRobot.negativeAttendeeMeet("123")

        meetPeopleViewRobot.assertViewStates(
                PeopleViewState(),
                PeopleViewState()
        )
    }
}
package com.caldi.meetpeople

import com.caldi.meetpeople.list.AnswerViewState
import com.caldi.meetpeople.models.AttendeeProfile
import com.caldi.meetpeople.personprofile.PersonProfileViewState
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.eq
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Test

class MeetPeopleViewModelTest {

    private val meetPeopleInteractor: MeetPeopleInteractor = mock()
    private val meetPeopleViewModel: MeetPeopleViewModel = MeetPeopleViewModel(meetPeopleInteractor)

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
        whenever(meetPeopleInteractor.fetchAttendeesProfiles(any())).thenReturn(
                Observable.just(PartialMeetPeopleViewState.SuccessfulAttendeesFetchState(attendeesList)))
        whenever(meetPeopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        val meetPeopleViewRobot = MeetPeopleViewRobot(meetPeopleViewModel)

        meetPeopleViewRobot.triggerProfilesFetching()

        meetPeopleViewRobot.assertViewStates(
                MeetPeopleViewState(),
                MeetPeopleViewState(progress = true),
                MeetPeopleViewState(personProfileViewStateList = listOf(PersonProfileViewState(
                        "123", "Matt", "url/to/pic",
                        "user/url",
                        listOf(AnswerViewState("What is your favourite color?", "Red")))))
        )
    }

    @Test
    fun testNoProfilesFetching() {
        whenever(meetPeopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Observable.just(PartialMeetPeopleViewState.BlankEventProfileState())
        )
        val meetPeopleViewRobot = MeetPeopleViewRobot(meetPeopleViewModel)

        meetPeopleViewRobot.assertViewStates(
                MeetPeopleViewState(eventProfileBlank = true)
        )
    }

    @Test
    fun testSuccessfulPositiveMeetAttendeeSave() {
        whenever(meetPeopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        whenever(meetPeopleInteractor.saveMetAttendee(any(), any(), eq(MeetPeopleInteractor.MeetType.POSITIVE)))
                .thenReturn(Observable.just(PartialMeetPeopleViewState.SuccessfulMetAttendeeSave()))
        val meetPeopleViewRobot = MeetPeopleViewRobot(meetPeopleViewModel)

        meetPeopleViewRobot.positiveAttendeeMeet("123")

        meetPeopleViewRobot.assertViewStates(
                MeetPeopleViewState(),
                MeetPeopleViewState()
        )
    }

    @Test
    fun testSuccessfulNegativeMeetAttendeeSave() {
        whenever(meetPeopleInteractor.checkIfEventProfileIsFilled(any())).thenReturn(
                Completable.complete().toObservable()
        )
        whenever(meetPeopleInteractor.saveMetAttendee(any(), any(), eq(MeetPeopleInteractor.MeetType.NEGATIVE)))
                .thenReturn(Observable.just(PartialMeetPeopleViewState.SuccessfulMetAttendeeSave()))
        val meetPeopleViewRobot = MeetPeopleViewRobot(meetPeopleViewModel)

        meetPeopleViewRobot.negativeAttendeeMeet("123")

        meetPeopleViewRobot.assertViewStates(
                MeetPeopleViewState(),
                MeetPeopleViewState()
        )
    }
}
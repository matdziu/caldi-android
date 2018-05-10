package com.caldi.home

import com.caldi.home.models.Event
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class HomeViewModelTest {

    private val homeInteractor: HomeInteractor = mock()
    private val homeViewModel = HomeViewModel(homeInteractor)

    @Test
    fun testEventsFetchingSuccess() {
        whenever(homeInteractor.fetchUserEvents()).thenReturn(
                Observable.just(PartialHomeViewState.FetchingSucceeded(listOf(Event()))))
        val homeViewRobot = HomeViewRobot(homeViewModel)

        homeViewRobot.emitEventsFetchTrigger()

        homeViewRobot.assertViewStates(HomeViewState(),
                HomeViewState(inProgress = true),
                HomeViewState(eventList = listOf(Event())))
    }

    @Test
    fun testEventsFetchingError() {
        whenever(homeInteractor.fetchUserEvents()).thenReturn(
                Observable.just(PartialHomeViewState.ErrorState()))
        val homeViewRobot = HomeViewRobot(homeViewModel)

        homeViewRobot.emitEventsFetchTrigger()

        homeViewRobot.assertViewStates(HomeViewState(),
                HomeViewState(inProgress = true),
                HomeViewState(error = true))
    }

    @Test
    fun testNotificationTokenSave() {
        whenever(homeInteractor.saveNotificationToken(any())).thenReturn(
                Observable.just(PartialHomeViewState.NotificationTokenSaveSuccess()))
        val homeViewRobot = HomeViewRobot(homeViewModel)

        homeViewRobot.emitNotificationToken("testToken")

        homeViewRobot.assertViewStates(HomeViewState(), HomeViewState())
    }
}
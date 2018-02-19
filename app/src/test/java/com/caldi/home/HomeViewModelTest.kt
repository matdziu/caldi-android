package com.caldi.home

import com.caldi.models.Event
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class HomeViewModelTest {

    private val homeInteractor: HomeInteractor = mock()
    private val homeViewModel = HomeViewModel(homeInteractor)

    @Test
    fun testEventsFetchingSuccess() {
        whenever(homeInteractor.fetchEvents()).thenReturn(
                Observable.just(PartialHomeViewState.FetchingSucceeded(listOf(Event()))))
        val homeViewRobot = HomeViewRobot(homeViewModel)

        homeViewRobot.emitEventsFetchTrigger()

        homeViewRobot.assertViewStates(HomeViewState(),
                HomeViewState(inProgress = true),
                HomeViewState(eventList = listOf(Event())))
    }

    @Test
    fun testEventsFetchingError() {
        whenever(homeInteractor.fetchEvents()).thenReturn(
                Observable.just(PartialHomeViewState.ErrorState()))
        val homeViewRobot = HomeViewRobot(homeViewModel)

        homeViewRobot.emitEventsFetchTrigger()

        homeViewRobot.assertViewStates(HomeViewState(),
                HomeViewState(inProgress = true),
                HomeViewState(error = true))
    }
}
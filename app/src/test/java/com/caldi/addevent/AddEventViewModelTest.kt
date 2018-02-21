package com.caldi.addevent

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class AddEventViewModelTest {

    private val addEventInteractor: AddEventInteractor = mock()
    private val addEventViewModel = AddEventViewModel(addEventInteractor)

    @Test
    fun testAddingNewEventWithSuccess() {
        val addEventViewRobot = AddEventViewRobot(addEventViewModel)
        whenever(addEventInteractor.addNewEvent(any())).thenReturn(Observable.just(PartialAddEventViewState.SuccessState()))

        addEventViewRobot.emitAddNewEventButtonClick("testEventCode")

        addEventViewRobot.assertViewStates(AddEventViewState(),
                AddEventViewState(inProgress = true),
                AddEventViewState(success = true))
    }

    @Test
    fun testAddingNewEventWithError() {
        val addEventViewRobot = AddEventViewRobot(addEventViewModel)
        whenever(addEventInteractor.addNewEvent(any())).thenReturn(Observable.just(PartialAddEventViewState.ErrorState()))

        addEventViewRobot.emitAddNewEventButtonClick("testEventCode")

        addEventViewRobot.assertViewStates(AddEventViewState(),
                AddEventViewState(inProgress = true),
                AddEventViewState(error = true))
    }
}
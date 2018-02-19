package com.caldi.home

import com.caldi.models.Event

sealed class PartialHomeViewState {

    class InProgressState : PartialHomeViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialHomeViewState()

    class FetchingSucceeded(val eventList: List<Event>) : PartialHomeViewState()
}
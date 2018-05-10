package com.caldi.home

import com.caldi.home.models.Event

sealed class PartialHomeViewState {

    class InProgressState : PartialHomeViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialHomeViewState()

    class FetchingSucceeded(val eventList: List<Event> = listOf()) : PartialHomeViewState()

    class NotificationTokenSaveSuccess : PartialHomeViewState()
}
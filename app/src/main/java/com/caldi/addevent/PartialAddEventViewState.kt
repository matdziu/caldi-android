package com.caldi.addevent

sealed class PartialAddEventViewState {

    class InProgressState : PartialAddEventViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialAddEventViewState()

    class SuccessState : PartialAddEventViewState()
}
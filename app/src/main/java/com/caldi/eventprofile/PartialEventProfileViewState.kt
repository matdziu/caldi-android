package com.caldi.eventprofile

import com.caldi.eventprofile.models.Question

sealed class PartialEventProfileViewState {

    class SuccessState(val questionsList: List<Question>) : PartialEventProfileViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class ProgressState : PartialEventProfileViewState()
}
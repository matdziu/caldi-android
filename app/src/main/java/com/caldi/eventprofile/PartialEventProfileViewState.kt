package com.caldi.eventprofile

import com.caldi.eventprofile.models.Question

sealed class PartialEventProfileViewState {

    class SuccessState(val questionsList: List<Question>) : PartialEventProfileViewState()

    class ErrorState(val dismissToast: Boolean) : PartialEventProfileViewState()

    class ProgressState : PartialEventProfileViewState()
}
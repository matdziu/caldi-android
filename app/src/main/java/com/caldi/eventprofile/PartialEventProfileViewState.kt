package com.caldi.eventprofile

import com.caldi.eventprofile.models.EventProfileData

sealed class PartialEventProfileViewState {

    class SuccessfulFetchState(val eventProfileData: EventProfileData = EventProfileData(), val renderEventName: Boolean)
        : PartialEventProfileViewState()

    class SuccessfulUpdateState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class ProgressState : PartialEventProfileViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class LocalValidation(val eventUserNameValid: Boolean) : PartialEventProfileViewState()
}
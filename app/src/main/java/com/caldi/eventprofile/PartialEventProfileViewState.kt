package com.caldi.eventprofile

import com.caldi.eventprofile.models.EventProfileData

sealed class PartialEventProfileViewState {

    class SuccessfulFetchState(val eventProfileData: EventProfileData) : PartialEventProfileViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class ProgressState : PartialEventProfileViewState()

    class SuccessfulUpdateState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class LocalValidation(val eventUserNameValid: Boolean) : PartialEventProfileViewState()
}
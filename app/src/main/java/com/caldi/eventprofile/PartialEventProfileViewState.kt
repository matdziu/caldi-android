package com.caldi.eventprofile

import com.caldi.common.models.EventProfileData

sealed class PartialEventProfileViewState {

    data class SuccessfulFetchState(val eventProfileData: EventProfileData = EventProfileData(),
                                    val questions: Map<String, String> = mapOf(),
                                    val renderInputs: Boolean = true) : PartialEventProfileViewState()

    class SuccessfulUpdateState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class SuccessfulPictureUploadState(val profilePictureUrl: String) : PartialEventProfileViewState()

    class ProgressState : PartialEventProfileViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    data class LocalValidation(val eventProfileData: EventProfileData,
                               val eventUserNameValid: Boolean,
                               val answerValidMap: Map<String, Boolean> = mapOf(),
                               val renderInputs: Boolean = true) : PartialEventProfileViewState()
}
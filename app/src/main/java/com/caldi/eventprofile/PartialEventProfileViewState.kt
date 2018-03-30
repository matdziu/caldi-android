package com.caldi.eventprofile

import com.caldi.common.models.Answer
import com.caldi.common.models.Question
import com.caldi.eventprofile.models.EventProfileData

sealed class PartialEventProfileViewState {

    class SuccessfulFetchState(val eventProfileData: EventProfileData = EventProfileData(), val renderInputs: Boolean)
        : PartialEventProfileViewState()

    class SuccessfulUpdateState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class SuccessfulPictureUploadState(val pictureUrl: String = "") : PartialEventProfileViewState()

    class ProgressState : PartialEventProfileViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    data class LocalValidation(val eventUserName: String, val eventUserNameValid: Boolean, val answerList: List<Answer>,
                               val questionList: List<Question>, val renderInputs: Boolean = true) : PartialEventProfileViewState()
}
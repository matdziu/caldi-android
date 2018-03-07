package com.caldi.eventprofile

import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.EventProfileData
import com.caldi.eventprofile.models.Question

sealed class PartialEventProfileViewState {

    class SuccessfulFetchState(val eventProfileData: EventProfileData = EventProfileData(), val renderEventName: Boolean)
        : PartialEventProfileViewState()

    class SuccessfulUpdateState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class SuccessfulPictureUploadState(val pictureUrl: String) : PartialEventProfileViewState()

    class ProgressState : PartialEventProfileViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialEventProfileViewState()

    class LocalValidation(val eventUserName: String, val eventUserNameValid: Boolean, val answerList: List<Answer>,
                          val questionList: List<Question>) : PartialEventProfileViewState()
}
package com.caldi.eventprofile

import com.caldi.eventprofile.list.QuestionViewState

data class EventProfileViewState(val eventUserName: String = "",
                                 val profilePictureUrl: String = "",
                                 val userLinkUrl: String = "",
                                 val questionViewStates: List<QuestionViewState> = listOf(),
                                 val updateSuccess: Boolean = false,
                                 val progress: Boolean = false,
                                 val error: Boolean = false,
                                 val dismissToast: Boolean = false,
                                 val eventUserNameValid: Boolean = true,
                                 val renderInputs: Boolean = false)
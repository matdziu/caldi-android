package com.caldi.eventprofile

import com.caldi.eventprofile.list.QuestionViewState

data class EventProfileViewState(val successUpload: Boolean = false,
                                 val progress: Boolean = false,
                                 val error: Boolean = false,
                                 val dismissToast: Boolean = false,
                                 val eventUserName: String = "",
                                 val eventUserNameValid: Boolean = true,
                                 val renderEventName: Boolean = false,
                                 val profilePictureUrl: String? = null,
                                 val questionViewStateList: List<QuestionViewState> = arrayListOf())
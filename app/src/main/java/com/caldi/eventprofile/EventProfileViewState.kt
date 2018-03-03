package com.caldi.eventprofile

import com.caldi.eventprofile.list.QuestionViewState

data class EventProfileViewState(val successFetch: Boolean = false,
                                 val successUpload: Boolean = false,
                                 val error: Boolean = false,
                                 val progress: Boolean = false,
                                 val dismissToast: Boolean = false,
                                 val questionViewStateList: List<QuestionViewState> = arrayListOf())
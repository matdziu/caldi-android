package com.caldi.eventprofile.models

import com.caldi.common.models.Answer
import com.caldi.common.models.Question

data class EventProfileData(val eventUserName: String = "",
                            val answerList: List<Answer> = listOf(),
                            val questionList: List<Question> = listOf(),
                            val profilePictureUrl: String = "",
                            val userLinkUrl: String = "",
                            var eventUserNameValid: Boolean = true)
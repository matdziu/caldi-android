package com.caldi.meetpeople.models

import com.caldi.common.models.Answer
import com.caldi.common.models.Question

data class AttendeeProfile(val userId: String = "",
                           val eventUserName: String = "",
                           val userLinkUrl: String = "",
                           val profilePictureUrl: String = "",
                           val answerList: List<Answer> = listOf(),
                           var questionList: List<Question> = listOf())
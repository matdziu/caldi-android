package com.caldi.meetpeople.models

import com.caldi.base.models.Answer
import com.caldi.base.models.Question

data class AttendeeProfile(val eventUserName: String = "",
                           val profilePicture: String = "",
                           val answerList: List<Answer> = listOf(),
                           var questionList: List<Question> = listOf())
package com.caldi.eventprofile.models

import com.caldi.base.models.Answer
import com.caldi.base.models.Question

data class EventProfileData(val eventUserName: String = "", val answerList: List<Answer> = listOf(),
                            val questionList: List<Question> = listOf(), val profilePictureUrl: String = "")
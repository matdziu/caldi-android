package com.caldi.eventprofile.models

data class EventProfileData(val eventUserName: String = "", val answerList: List<Answer> = listOf(),
                            val questionList: List<Question> = listOf(), val profilePictureUrl: String = "")
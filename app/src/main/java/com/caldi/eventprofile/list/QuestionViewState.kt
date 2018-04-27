package com.caldi.eventprofile.list

data class QuestionViewState(var questionText: String = "",
                             var answerText: String = "",
                             val questionId: String = "",
                             val answerValid: Boolean = true)
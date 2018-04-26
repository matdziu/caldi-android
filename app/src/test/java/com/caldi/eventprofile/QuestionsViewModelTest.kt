package com.caldi.eventprofile

import com.caldi.eventprofile.list.QuestionViewState
import com.caldi.common.models.Answer
import com.caldi.common.models.Question
import junit.framework.Assert.assertEquals
import org.junit.Test

class QuestionsViewModelTest {

    private val questionsViewModel = QuestionsViewModel()

    @Test
    fun testGettingAnswerObjects() {
        val questionItemViewRobot = QuestionItemViewRobot(questionsViewModel)
        questionItemViewRobot.init(QuestionViewState("What's your first name?",
                "", "1"))
        questionItemViewRobot.assertViewStates(
                QuestionViewState("What's your first name?", "", "1")
        )

        questionItemViewRobot.emitUserInput("")
        questionItemViewRobot.emitUserInput("m")
        questionItemViewRobot.emitUserInput("ma")
        questionItemViewRobot.emitUserInput("mat")
        questionItemViewRobot.emitUserInput("matt")

        assertEquals(listOf(Answer("1", "matt")), questionsViewModel.getAnswerList())
    }

    @Test
    fun testGettingQuestionObjects() {
        val questionItemViewRobot = QuestionItemViewRobot(questionsViewModel)
        questionItemViewRobot.init(QuestionViewState("What's your first name?",
                "matt", "1"))
        questionItemViewRobot.assertViewStates(
                QuestionViewState("What's your first name?", "matt", "1")
        )

        assertEquals(listOf(Question("1", "What's your first name?")), questionsViewModel.getQuestionList())
    }
}
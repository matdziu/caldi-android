package com.caldi.eventprofile.list

import android.arch.lifecycle.ViewModel
import com.caldi.common.models.Answer
import com.caldi.common.models.Question
import io.reactivex.disposables.Disposable

class QuestionsViewModel : ViewModel() {

    val defaultViewStateList = arrayListOf<QuestionViewState>()
    private val answerList = arrayListOf<Answer>()
    private val disposableList = arrayListOf<Disposable>()

    fun bind(questionItemView: QuestionItemView, position: Int) {
        questionItemView.defaultRender(defaultViewStateList[position])

        val answer = answerList[position]
        disposableList.add(questionItemView.emitUserInput()
                .map { answer.copy(answer = it) }
                .subscribe { answerList[position] = it })
    }

    fun setQuestionItemStateList(questionItemStateList: List<QuestionViewState>) {
        unbindAll()
        defaultViewStateList.clear()
        answerList.clear()
        defaultViewStateList.addAll(questionItemStateList)
        questionItemStateList.mapTo(answerList) { Answer(it.questionId, it.answerText, it.answerValid) }
    }

    fun getAnswerList(): List<Answer> {
        return answerList
    }

    fun getQuestionList(): List<Question> {
        return defaultViewStateList.map { Question(it.questionId, it.questionText) }
    }

    fun getItemCount(): Int = defaultViewStateList.size

    private fun unbindAll() {
        for (disposable in disposableList) {
            disposable.dispose()
        }
        disposableList.clear()
    }
}
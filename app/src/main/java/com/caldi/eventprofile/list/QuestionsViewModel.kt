package com.caldi.eventprofile.list

import android.arch.lifecycle.ViewModel
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.Question
import io.reactivex.disposables.Disposable

class QuestionsViewModel : ViewModel() {

    private val defaultViewStateList = arrayListOf<QuestionViewState>()
    private val disposableList = arrayListOf<Disposable>()

    fun bind(questionItemView: QuestionItemView, position: Int) {
        val defaultViewState = defaultViewStateList[position]
        questionItemView.defaultRender(defaultViewState)

        disposableList.add(questionItemView.emitUserInput()
                .map { defaultViewState.copy(answerText = it) }
                .subscribe { defaultViewStateList[position] = it })
    }

    fun setQuestionItemStateList(questionItemStateList: List<QuestionViewState>) {
        defaultViewStateList.clear()
        defaultViewStateList.addAll(questionItemStateList)
    }

    fun getAnswerList(): List<Answer> {
        return defaultViewStateList.map { Answer(it.questionId, it.answerText) }
    }

    fun getQuestionList(): List<Question> {
        return defaultViewStateList.map { Question(it.questionId, it.questionText) }
    }

    fun getItemCount(): Int = defaultViewStateList.size

    fun unbindAll() {
        for (disposable in disposableList) {
            disposable.dispose()
        }
        disposableList.clear()
    }
}
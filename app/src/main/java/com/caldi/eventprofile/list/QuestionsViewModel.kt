package com.caldi.eventprofile.list

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable

class QuestionsViewModel : ViewModel() {

    private val disposableList = arrayListOf<Disposable>()
    private val questionItemStateList = arrayListOf<QuestionViewState>()

    fun bind(questionItemView: QuestionItemView, position: Int) {
        defaultRender(questionItemView, position)

        disposableList.add(questionItemView.emitUserInput()
                .map { questionItemStateList[position].copy(answerText = it) }
                .subscribe { questionItemStateList[position] = it })
    }


    private fun defaultRender(questionItemView: QuestionItemView, position: Int) {
        val currentQuestionItemState = questionItemStateList[position]
        questionItemView.render(currentQuestionItemState)
    }

    fun setQuestionItemStateList(questionItemStateList: List<QuestionViewState>) {
        if (this.questionItemStateList.size == 0) {
            this.questionItemStateList.addAll(questionItemStateList)
        }
    }

    fun getItemCount(): Int = questionItemStateList.size

    fun unbindAll() {
        for (disposable in disposableList) {
            disposable.dispose()
        }
        disposableList.clear()
    }
}
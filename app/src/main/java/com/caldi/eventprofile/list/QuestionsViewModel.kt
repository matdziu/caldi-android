package com.caldi.eventprofile.list

import android.arch.lifecycle.ViewModel
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
        if (defaultViewStateList.size == 0) {
            defaultViewStateList.addAll(questionItemStateList)
        }
    }

    fun getItemCount(): Int = defaultViewStateList.size

    fun unbindAll() {
        for (disposable in disposableList) {
            disposable.dispose()
        }
        disposableList.clear()
    }
}
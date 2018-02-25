package com.caldi.eventprofile.list

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class QuestionsViewModel : ViewModel() {

    private val stateSubjectsList = arrayListOf<BehaviorSubject<QuestionViewState>>()
    private val disposableList = arrayListOf<Disposable>()

    fun bind(questionItemView: QuestionItemView, position: Int) {
        defaultRender(questionItemView, position)

        val stateSubject = stateSubjectsList[position]

        disposableList.add(questionItemView.emitUserInput()
                .map { BehaviorSubject.createDefault(stateSubject.value.copy(answerText = it)) }
                .subscribe { stateSubjectsList[position] = it })
    }

    private fun defaultRender(questionItemView: QuestionItemView, position: Int) {
        questionItemView.render(stateSubjectsList[position].value)
    }

    fun setQuestionItemStateList(questionItemStateList: List<QuestionViewState>) {
        if (stateSubjectsList.size == 0) {
            questionItemStateList.mapTo(stateSubjectsList) { BehaviorSubject.createDefault(it) }
        }
    }

    fun getItemCount(): Int = stateSubjectsList.size

    fun unbindAll() {
        for (disposable in disposableList) {
            disposable.dispose()
        }
        disposableList.clear()
    }
}
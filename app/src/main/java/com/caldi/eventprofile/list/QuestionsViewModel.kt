package com.caldi.eventprofile.list

import com.caldi.eventprofile.models.Answer
import io.reactivex.disposables.CompositeDisposable

class QuestionsViewModel {

    private val compositeDisposable = CompositeDisposable()
    private val answersList: ArrayList<Answer> = arrayListOf()

    fun bind(questionItemView: QuestionItemView) {
        compositeDisposable.add(questionItemView.emitAnswer()
                .subscribe({
                    answersList.add(it)
                    if (answersList.size == 3) {
                        answersList.clear()
                    }
                }))
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
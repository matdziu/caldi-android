package com.caldi.eventprofile.list

import android.arch.lifecycle.ViewModel
import com.caldi.eventprofile.models.Answer
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class QuestionsViewModel : ViewModel() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val answersList: ArrayList<Answer> = arrayListOf()
    private val answersSubject: Subject<ArrayList<Answer>> = PublishSubject.create()

    fun bindToAnswerFields(questionItemView: QuestionItemView) {
        compositeDisposable.add(questionItemView.emitAnswer()
                .subscribe({
                    answersList.add(it)
                    if (answersList.size == 3) {
                        answersSubject.onNext(answersList)
                        answersList.clear()
                    }
                }))
    }

    fun unbind() {
        compositeDisposable.clear()
    }

    fun emitAnswersList(): Observable<ArrayList<Answer>> = answersSubject
}
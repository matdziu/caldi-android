package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.eventprofile.models.Answer
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_question.view.questionEditText
import kotlinx.android.synthetic.main.item_question.view.questionTextView

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), QuestionItemView {

    private lateinit var questionViewState: QuestionViewState

    fun bind(questionViewState: QuestionViewState) {
        this.questionViewState = questionViewState
        itemView.questionTextView.text = questionViewState.questionText
        itemView.questionEditText.setText(questionViewState.answerText)
    }

    override fun emitAnswer(): Observable<Answer> {
        return Observable.fromCallable({ Answer(questionViewState.questionId, itemView.questionTextView.text.toString()) })
    }
}
package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_question.view.questionEditText
import kotlinx.android.synthetic.main.item_question.view.questionTextView

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), QuestionItemView {

    override fun defaultRender(questionViewState: QuestionViewState) {
        itemView.questionTextView.text = questionViewState.questionText
        itemView.questionEditText.setText(questionViewState.answerText)
    }

    override fun emitUserInput(): Observable<String> {
        return RxTextView.textChanges(itemView.questionEditText).map { it.toString() }
    }
}
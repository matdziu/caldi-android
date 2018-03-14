package com.caldi.meetpeople.list

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_person_answer.view.answerTextView
import kotlinx.android.synthetic.main.item_person_answer.view.questionTextView

class AnswerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun render(answerViewState: AnswerViewState) {
        itemView.questionTextView.text = answerViewState.question
        itemView.answerTextView.text = answerViewState.answer
    }
}
package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_event_question.view.questionEditText
import kotlinx.android.synthetic.main.item_event_question.view.questionTextView

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val questionEditText = itemView.questionEditText

    fun bind(questionViewState: QuestionViewState) {
        questionEditText.setText(questionViewState.answerText)
        itemView.questionTextView.text = questionViewState.questionText
        itemView.questionEditText.showError(!questionViewState.answerValid)
    }
}
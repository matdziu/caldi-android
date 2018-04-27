package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.customviews.CaldiEditText
import kotlinx.android.synthetic.main.item_event_question.view.questionEditText
import kotlinx.android.synthetic.main.item_event_question.view.questionTextView

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val questionEditText: CaldiEditText = itemView.questionEditText

    fun bind(questionViewState: QuestionViewState) {
        questionEditText.setText(questionViewState.answerText)
        with(itemView) {
            questionTextView.text = questionViewState.questionText
            questionEditText.showError(!questionViewState.answerValid)
        }
    }
}
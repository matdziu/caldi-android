package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.eventprofile.models.Question
import kotlinx.android.synthetic.main.item_question.view.questionTextView

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(question: Question) {
        itemView.questionTextView.text = question.text
    }
}
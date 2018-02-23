package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.Question
import io.reactivex.Observable
import kotlinx.android.synthetic.main.item_question.view.questionTextView

class QuestionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), QuestionItemView {

    private lateinit var question: Question

    fun bind(question: Question) {
        this.question = question
        itemView.questionTextView.text = question.text
    }

    override fun emitAnswer(): Observable<Answer> {
        return Observable.fromCallable({ Answer(question.id, itemView.questionTextView.text.toString()) })
    }
}
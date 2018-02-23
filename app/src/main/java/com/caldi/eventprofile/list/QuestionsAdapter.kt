package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.eventprofile.models.Answer
import com.caldi.eventprofile.models.Question
import io.reactivex.Observable

class QuestionsAdapter : RecyclerView.Adapter<QuestionViewHolder>() {

    private val questionsList = arrayListOf<Question>()
    private var shouldEmitAnswers = false

    lateinit var questionsViewModel: QuestionsViewModel

    override fun getItemCount(): Int = questionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_question,
                parent, false)
        return QuestionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questionsList[position])

        if (shouldEmitAnswers) {
            questionsViewModel.bindToAnswerFields(holder)
        }
    }

    fun setQuestionsList(questionsList: List<Question>) {
        this.questionsList.clear()
        this.questionsList.addAll(questionsList)
        notifyDataSetChanged()
    }

    fun emitAnswers(): Observable<ArrayList<Answer>> {
        shouldEmitAnswers = true
        notifyDataSetChanged()
        return questionsViewModel.emitAnswersList().doOnNext({ shouldEmitAnswers = false })
    }
}
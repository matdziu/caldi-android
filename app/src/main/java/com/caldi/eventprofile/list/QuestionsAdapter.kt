package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.eventprofile.models.Question

class QuestionsAdapter(private val questionsViewModel: QuestionsViewModel) : RecyclerView.Adapter<QuestionViewHolder>() {

    private val questionsList = arrayListOf<Question>()

    override fun getItemCount(): Int = questionsList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_question,
                parent, false)
        return QuestionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questionsList[position])
        questionsViewModel.bind(holder)
    }

    fun setQuestionsList(questionsList: List<Question>) {
        this.questionsList.clear()
        this.questionsList.addAll(questionsList)
        notifyDataSetChanged()
    }
}
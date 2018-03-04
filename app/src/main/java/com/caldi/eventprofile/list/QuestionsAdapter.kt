package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R

class QuestionsAdapter(private val questionsViewModel: QuestionsViewModel)
    : RecyclerView.Adapter<QuestionViewHolder>() {

    override fun getItemCount(): Int = questionsViewModel.getItemCount()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_question,
                parent, false)
        return QuestionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        questionsViewModel.bind(holder, position)
    }

    fun setQuestionsList(questionViewStatesList: List<QuestionViewState>) {
        if (questionViewStatesList.isNotEmpty() && questionViewStatesList != questionsViewModel.defaultViewStateList) {
            questionsViewModel.setQuestionItemStateList(questionViewStatesList)
            notifyDataSetChanged()
        }
    }
}
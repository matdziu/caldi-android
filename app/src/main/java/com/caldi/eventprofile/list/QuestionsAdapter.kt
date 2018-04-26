package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R

class QuestionsAdapter : RecyclerView.Adapter<QuestionViewHolder>() {

    private val questionViewStates = arrayListOf<QuestionViewState>()
    val answers: HashMap<String, String> = hashMapOf()

    override fun getItemCount(): Int = questionViewStates.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event_question,
                parent, false)
        return QuestionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        holder.bind(questionViewStates[position]).subscribe { answers[it.first] = it.second }
    }

    fun setQuestionViewStates(questionViewStates: List<QuestionViewState>) {
        if (questionViewStates.isNotEmpty()) {
            this.questionViewStates.clear()
            this.questionViewStates.addAll(questionViewStates)
            notifyDataSetChanged()
        }
    }
}
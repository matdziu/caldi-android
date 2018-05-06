package com.caldi.people.meetpeople.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R

class AnswersAdapter : RecyclerView.Adapter<AnswerViewHolder>() {

    private val answerViewStateList = arrayListOf<AnswerViewState>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_person_answer,
                parent, false)
        return AnswerViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int) {
        holder.render(answerViewStateList[position])
    }

    override fun getItemCount(): Int = answerViewStateList.size

    fun setAnswerList(answerViewStateList: List<AnswerViewState>) {
        this.answerViewStateList.clear()
        this.answerViewStateList.addAll(answerViewStateList)
        notifyDataSetChanged()
    }
}
package com.caldi.eventprofile.list

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.disposables.CompositeDisposable

class QuestionsAdapter : RecyclerView.Adapter<QuestionViewHolder>() {

    private val questionViewStates = arrayListOf<QuestionViewState>()
    private val compositeDisposable = CompositeDisposable()

    val answers: HashMap<String, String> = hashMapOf()

    override fun getItemCount(): Int = questionViewStates.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuestionViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_event_question,
                parent, false)
        return QuestionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: QuestionViewHolder, position: Int) {
        val currentQuestionViewState = questionViewStates[position]
        holder.bind(currentQuestionViewState)
        compositeDisposable.add(RxTextView.textChanges(holder.questionEditText)
                .map { it.toString() }
                .subscribe { answers[currentQuestionViewState.questionId] = it })
    }

    fun setQuestionViewStates(questionViewStates: List<QuestionViewState>) {
        if (questionViewStates.isNotEmpty() && this.questionViewStates != questionViewStates) {
            compositeDisposable.clear()
            this.questionViewStates.clear()
            this.questionViewStates.addAll(questionViewStates)
            notifyDataSetChanged()
        }
    }
}
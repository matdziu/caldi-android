package com.caldi.filterpeople.list

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.filterpeople.spinner.FilterType
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_person.view.personImageView
import kotlinx.android.synthetic.main.item_person.view.personTextView

class PersonProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(personProfileViewState: PersonProfileViewState, filterType: FilterType?) {
        with(itemView) {
            displayDesiredText(personTextView, personProfileViewState, filterType)
            Picasso.get()
                    .load(personProfileViewState.profilePictureUrl)
                    .placeholder(R.drawable.profile_picture_shape)
                    .into(personImageView)
        }
    }

    private fun displayDesiredText(personTextView: TextView, personProfileViewState: PersonProfileViewState,
                                   filterType: FilterType?) {
        when (filterType) {
            is FilterType.NameFilterType -> personTextView.text = personProfileViewState.eventUserName
            is FilterType.LinkFilterType -> personTextView.text = personProfileViewState.userLinkUrl
            is FilterType.QuestionFilterType -> personTextView.text = searchForAnswer(filterType.text, personProfileViewState)
        }
    }

    private fun searchForAnswer(desiredQuestion: String, personProfileViewState: PersonProfileViewState): String {
        for (answerViewState in personProfileViewState.answerViewStateList) {
            if (answerViewState.question == desiredQuestion) {
                return answerViewState.answer
            }
        }
        return ""
    }
}
package com.caldi.people.filterpeople.list

import android.support.v7.widget.RecyclerView
import android.text.util.Linkify
import android.view.View
import android.widget.TextView
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.injection.modules.GlideApp
import com.caldi.people.filterpeople.spinner.FilterType
import kotlinx.android.synthetic.main.item_person.view.personImageView
import kotlinx.android.synthetic.main.item_person.view.personTextView

class PersonProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(personProfileViewState: PersonProfileViewState, filterType: FilterType?) {
        with(itemView) {
            personTextView.autoLinkMask = 0
            displayDesiredText(personTextView, personProfileViewState, filterType)
            loadProfilePicture(personProfileViewState.profilePictureUrl, this)
        }
    }

    private fun loadProfilePicture(profilePictureUrl: String, itemView: View) {
        if (profilePictureUrl.isNotEmpty()) {
            GlideApp.with(itemView)
                    .load(profilePictureUrl)
                    .placeholder(R.drawable.profile_picture_shape)
                    .into(itemView.personImageView)
        } else {
            GlideApp.with(itemView)
                    .load(R.drawable.profile_picture_shape)
                    .into(itemView.personImageView)
        }
    }

    private fun displayDesiredText(personTextView: TextView, personProfileViewState: PersonProfileViewState,
                                   filterType: FilterType?) {
        when (filterType) {
            is FilterType.NameFilterType -> personTextView.text = personProfileViewState.eventUserName
            is FilterType.LinkFilterType -> displayUserLink(personTextView, personProfileViewState.userLinkUrl)
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

    private fun displayUserLink(personTextView: TextView, userLinkUrl: String) {
        val formattedUserLink = if (userLinkUrl.isBlank()) "-" else userLinkUrl
        personTextView.autoLinkMask = Linkify.WEB_URLS
        personTextView.text = formattedUserLink
    }
}
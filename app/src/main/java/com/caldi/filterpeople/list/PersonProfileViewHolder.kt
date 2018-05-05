package com.caldi.filterpeople.list

import android.support.v7.widget.RecyclerView
import android.view.View
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_person.view.personImageView
import kotlinx.android.synthetic.main.item_person.view.personTextView

class PersonProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(personProfileViewState: PersonProfileViewState) {
        with(itemView) {
            personTextView.text = personProfileViewState.eventUserName
            Picasso.get()
                    .load(personProfileViewState.profilePictureUrl)
                    .placeholder(R.drawable.profile_picture_shape)
                    .into(personImageView)
        }
    }
}
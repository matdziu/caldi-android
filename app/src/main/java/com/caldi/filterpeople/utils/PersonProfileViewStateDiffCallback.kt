package com.caldi.filterpeople.utils

import android.support.v7.util.DiffUtil
import com.caldi.common.states.PersonProfileViewState

class PersonProfileViewStateDiffCallback : DiffUtil.ItemCallback<PersonProfileViewState>() {

    override fun areItemsTheSame(oldItem: PersonProfileViewState, newItem: PersonProfileViewState): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: PersonProfileViewState, newItem: PersonProfileViewState): Boolean {
        return oldItem == newItem
    }
}
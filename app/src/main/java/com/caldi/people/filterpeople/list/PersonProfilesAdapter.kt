package com.caldi.people.filterpeople.list

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.LayoutInflater
import android.view.ViewGroup
import com.caldi.R
import com.caldi.common.states.PersonProfileViewState
import com.caldi.people.filterpeople.FilterPeopleActivity
import com.caldi.people.filterpeople.spinner.FilterType
import com.caldi.people.filterpeople.utils.PersonProfileViewStateDiffCallback

class PersonProfilesAdapter(private val filterPeopleActivity: FilterPeopleActivity) :
        ListAdapter<PersonProfileViewState, PersonProfileViewHolder>(PersonProfileViewStateDiffCallback()) {

    private val currentProfileViewStateList = arrayListOf<PersonProfileViewState>()

    var filterType: FilterType? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonProfileViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_person, parent, false)
        return PersonProfileViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PersonProfileViewHolder, position: Int) {
        val currentPersonProfile = getItem(position)
        holder.bind(currentPersonProfile, filterType)
        holder.itemView.setOnClickListener {
            with(filterPeopleActivity) {
                if (!viewPersonProfileMode) {
                    addPersonProfileFragment(currentPersonProfile)
                    enableViewPersonProfileMode(true)
                }
            }
        }
    }

    fun addProfilesBatch(personProfileViewStateList: List<PersonProfileViewState>) {
        currentProfileViewStateList.addAll(personProfileViewStateList)
        submitList(currentProfileViewStateList)
    }
}
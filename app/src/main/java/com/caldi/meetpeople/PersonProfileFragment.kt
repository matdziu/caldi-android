package com.caldi.meetpeople

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldi.R
import kotlinx.android.synthetic.main.fragment_person_profile.acceptProfileButton
import kotlinx.android.synthetic.main.fragment_person_profile.dismissProfileButton

class PersonProfileFragment : Fragment() {

    private lateinit var hostingActivity: MeetPeopleActivity

    companion object {

        fun newInstance(): PersonProfileFragment = PersonProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_person_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        hostingActivity = activity as MeetPeopleActivity
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dismissProfileButton.setOnClickListener { emitTagForDismiss(tag) }
        acceptProfileButton.setOnClickListener { emitTagForAccept(tag) }
    }

    private fun emitTagForDismiss(tag: String?) {
        tag?.let { hostingActivity.dismissProfileSubject.onNext(it) }
    }

    private fun emitTagForAccept(tag: String?) {
        tag?.let { hostingActivity.acceptProfileSubject.onNext(it) }
    }
}
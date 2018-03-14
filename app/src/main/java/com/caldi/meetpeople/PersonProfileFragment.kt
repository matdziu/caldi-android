package com.caldi.meetpeople

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.caldi.R

class PersonProfileFragment : Fragment() {

    companion object {

        fun newInstance(): PersonProfileFragment = PersonProfileFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_person_profile, container, false)
    }
}
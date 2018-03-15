package com.caldi.meetpeople

import android.arch.lifecycle.ViewModel
import android.util.Log

class MeetPeopleViewModel(private val meetPeopleInteractor: MeetPeopleInteractor) : ViewModel() {

    fun bind(meetPeopleView: MeetPeopleView) {
        meetPeopleInteractor.fetchAttendeesProfiles("8c86f3a8-8a29-4d3d-901d-8769738dc428")
                .subscribe {
                    Log.d("mateusz", "as")
                }
    }
}
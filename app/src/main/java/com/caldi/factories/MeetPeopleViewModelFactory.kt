package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.meetpeople.MeetPeopleInteractor
import com.caldi.meetpeople.MeetPeopleViewModel

@Suppress("UNCHECKED_CAST")
class MeetPeopleViewModelFactory(private val meetPeopleInteractor: MeetPeopleInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MeetPeopleViewModel(meetPeopleInteractor) as T
    }
}
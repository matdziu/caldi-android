package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.people.common.PeopleInteractor
import com.caldi.people.common.PeopleViewModel

@Suppress("UNCHECKED_CAST")
class PeopleViewModelFactory(private val peopleInteractor: PeopleInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return PeopleViewModel(peopleInteractor) as T
    }
}
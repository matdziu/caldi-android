package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.eventprofile.EventProfileInteractor
import com.caldi.eventprofile.EventProfileViewModel

@Suppress("UNCHECKED_CAST")
class EventProfileViewModelFactory(private val eventProfileInteractor: EventProfileInteractor)
    : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EventProfileViewModel(eventProfileInteractor) as T
    }
}
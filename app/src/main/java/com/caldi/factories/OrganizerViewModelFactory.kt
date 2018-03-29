package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.organizer.OrganizerInteractor
import com.caldi.organizer.OrganizerViewModel

@Suppress("UNCHECKED_CAST")
class OrganizerViewModelFactory(private val organizerInteractor: OrganizerInteractor) : ViewModelProvider.Factory {

    @Suppress
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OrganizerViewModel(organizerInteractor) as T
    }
}
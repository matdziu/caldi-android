package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.addevent.AddEventInteractor
import com.caldi.addevent.AddEventViewModel

@Suppress("UNCHECKED_CAST")
class AddEventViewModelFactory(private val addEventInteractor: AddEventInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AddEventViewModel(addEventInteractor) as T
    }
}
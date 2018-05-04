package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.filterpeople.FilterPeopleInteractor
import com.caldi.filterpeople.FilterPeopleViewModel

@Suppress("UNCHECKED_CAST")
class FilterPeopleViewModelFactory(private val filterPeopleInteractor: FilterPeopleInteractor) :
        ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FilterPeopleViewModel(filterPeopleInteractor) as T
    }
}
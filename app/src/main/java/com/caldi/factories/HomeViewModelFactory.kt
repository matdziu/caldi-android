package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.home.HomeInteractor
import com.caldi.home.HomeViewModel

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val homeInteractor: HomeInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(homeInteractor) as T
    }
}
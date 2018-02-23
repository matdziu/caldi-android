package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.eventprofile.list.QuestionsViewModel

@Suppress("UNCHECKED_CAST")
class QuestionsViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return QuestionsViewModel() as T
    }
}
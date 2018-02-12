package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.signup.SignUpInteractor
import com.caldi.signup.SignUpViewModel

@Suppress("UNCHECKED_CAST")
class SignUpViewModelFactory(private val signUpInteractor: SignUpInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SignUpViewModel(signUpInteractor) as T
    }
}
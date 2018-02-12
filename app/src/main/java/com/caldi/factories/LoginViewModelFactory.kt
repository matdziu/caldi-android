package com.caldi.factories

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.caldi.login.LoginInteractor
import com.caldi.login.LoginViewModel

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(private val loginInteractor: LoginInteractor) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LoginViewModel(loginInteractor) as T
    }
}
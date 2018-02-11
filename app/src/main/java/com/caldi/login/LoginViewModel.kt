package com.caldi.login

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialLoginViewState>()

    fun bind(loginView: LoginView) {

    }

    private fun reduceState(previousState: LoginViewState, partialState: PartialLoginViewState)
            : LoginViewState {
        return when (partialState) {
            is PartialLoginViewState.LocalValidation -> LoginViewState(emailValid = partialState.emailValid,
                    passwordValid = partialState.passwordValid)
            is PartialLoginViewState.InProgressState -> LoginViewState(true)
            is PartialLoginViewState.ErrorState -> LoginViewState(error = true, dismissToast = partialState.dismissToast)
            is PartialLoginViewState.LoginSuccess -> LoginViewState(loginSuccess = true)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
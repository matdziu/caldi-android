package com.caldi.login

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialLoginViewState>()

    fun bind(loginView: LoginView) {
        val inputDataObservable = loginView.emitInput()
                .flatMap { inputData ->
                    val emailValid = !inputData.email.isBlank()
                    val passwordValid = !inputData.password.isBlank()

                    return@flatMap if (!emailValid || !passwordValid) {
                        Observable.just(PartialLoginViewState.LocalValidation(emailValid, passwordValid))
                    } else {
                        loginRepository.login(inputData.email, inputData.password)
                                .startWith(PartialLoginViewState.InProgressState())
                    }
                }
                .subscribeWith(stateSubject)

        compositeDisposable.add(inputDataObservable.scan(LoginViewState(), this::reduceState)
                .subscribe({ loginView.render(it) }))
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
package com.caldi.signup

import android.arch.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class SignUpViewModel(private val signUpInteractor: SignUpInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialSignUpViewState>()

    fun bind(signUpView: SignUpView) {
        val inputDataObservable = signUpView.emitInput()
                .flatMap { inputData ->
                    val trimmedEmail = inputData.email.trim()
                    val trimmedPassword = inputData.password.trim()
                    val trimmedRepeatPassword = inputData.repeatPassword.trim()

                    val emailValid = !trimmedEmail.isBlank() && !trimmedEmail.contains(" ")
                    val passwordValid = trimmedPassword.length >= 6 && !trimmedPassword.contains(" ")
                    val repeatPasswordValid = !trimmedRepeatPassword.isBlank()
                            && trimmedPassword == trimmedRepeatPassword

                    return@flatMap if (!emailValid || !passwordValid || !repeatPasswordValid) {
                        Observable.just(PartialSignUpViewState.LocalValidation(emailValid,
                                passwordValid, repeatPasswordValid))
                    } else {
                        signUpInteractor.createAccount(trimmedEmail, trimmedPassword)
                                .startWith(PartialSignUpViewState.InProgressState())
                    }
                }
                .subscribeWith(stateSubject)

        compositeDisposable.add(inputDataObservable.scan(SignUpViewState(), this::reduceState)
                .subscribe({ signUpView.render(it) }))
    }

    private fun reduceState(previousState: SignUpViewState, partialState: PartialSignUpViewState)
            : SignUpViewState {
        return when (partialState) {
            is PartialSignUpViewState.LocalValidation -> SignUpViewState(emailValid = partialState.emailValid,
                    passwordValid = partialState.passwordValid, repeatPasswordValid = partialState.repeatPasswordValid)
            is PartialSignUpViewState.InProgressState -> SignUpViewState(true)
            is PartialSignUpViewState.ErrorState -> SignUpViewState(error = true, dismissToast = partialState.dismissToast)
            is PartialSignUpViewState.SignUpSuccess -> SignUpViewState(signUpSuccess = true)
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
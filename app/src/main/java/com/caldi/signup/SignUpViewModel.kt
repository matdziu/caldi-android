package com.caldi.signup

import android.arch.lifecycle.ViewModel
import com.caldi.R
import com.caldi.constants.ERROR_EMAIL_ALREADY_IN_USE
import com.caldi.constants.ERROR_INVALID_EMAIL
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthException
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class SignUpViewModel(private val signUpInteractor: SignUpInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.createDefault(SignUpViewState())

    fun bind(signUpView: SignUpView) {
        val inputDataObservable = signUpView.emitInput()
                .flatMap { inputData ->
                    val trimmedEmail = inputData.email.trim()
                    val trimmedPassword = inputData.password.trim()

                    val emailValid = !trimmedEmail.isBlank() && !trimmedEmail.contains(" ")
                    val passwordValid = trimmedPassword.length >= 6 && !trimmedPassword.contains(" ")

                    return@flatMap if (!emailValid || !passwordValid) {
                        Observable.just(PartialSignUpViewState.LocalValidation(emailValid, passwordValid))
                    } else {
                        signUpInteractor.createAccount(trimmedEmail, trimmedPassword)
                                .startWith(PartialSignUpViewState.InProgressState())
                    }
                }
                .scan(stateSubject.value, this::reduce)
                .subscribeWith(stateSubject)

        compositeDisposable.add(inputDataObservable.subscribe({ signUpView.render(it) }))
    }

    private fun reduce(previousState: SignUpViewState, partialState: PartialSignUpViewState)
            : SignUpViewState {
        return when (partialState) {
            is PartialSignUpViewState.LocalValidation -> SignUpViewState(
                    emailValid = partialState.emailValid,
                    passwordValid = partialState.passwordValid)
            is PartialSignUpViewState.InProgressState -> SignUpViewState(true)
            is PartialSignUpViewState.ErrorState -> SignUpViewState(
                    errorMessageId = getErrorMessageId(partialState.exception),
                    error = true,
                    dismissToast = partialState.dismissToast)
            is PartialSignUpViewState.SignUpSuccess -> SignUpViewState(
                    signUpSuccess = true)
        }
    }

    private fun getErrorMessageId(exception: Exception?): Int {
        return when (exception) {
            is FirebaseNetworkException -> R.string.no_internet_error
            is FirebaseAuthException -> getErrorMessageId(exception)
            else -> R.string.generic_error
        }
    }

    private fun getErrorMessageId(exception: FirebaseAuthException): Int {
        return when (exception.errorCode) {
            ERROR_EMAIL_ALREADY_IN_USE -> R.string.already_used_email_error
            ERROR_INVALID_EMAIL -> R.string.invalid_email_error
            else -> R.string.generic_error
        }
    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
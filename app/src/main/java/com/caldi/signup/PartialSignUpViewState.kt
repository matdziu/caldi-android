package com.caldi.signup

sealed class PartialSignUpViewState {

    class InProgressState : PartialSignUpViewState()

    data class LocalValidation(val emailValid: Boolean = false,
                               val passwordValid: Boolean = false,
                               val repeatPasswordValid: Boolean = false) : PartialSignUpViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialSignUpViewState()

    class SignUpSuccess : PartialSignUpViewState()
}
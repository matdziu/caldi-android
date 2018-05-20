package com.caldi.signup

import java.lang.Exception

sealed class PartialSignUpViewState {

    class InProgressState : PartialSignUpViewState()

    data class LocalValidation(val emailValid: Boolean = false,
                               val passwordValid: Boolean = false) : PartialSignUpViewState()

    data class ErrorState(val exception: Exception?,
                          val dismissToast: Boolean = false) : PartialSignUpViewState()

    class SignUpSuccess : PartialSignUpViewState()
}
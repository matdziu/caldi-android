package com.caldi.login

sealed class PartialLoginViewState {

    class InProgressState : PartialLoginViewState()

    data class LocalValidation(val emailValid: Boolean = false,
                               val passwordValid: Boolean = false) : PartialLoginViewState()

    class ErrorState(val dismissToast: Boolean = false) : PartialLoginViewState()

    class LoginSuccess : PartialLoginViewState()
}
package com.caldi.signup

data class SignUpViewState(val inProgress: Boolean = false,
                           val emailValid: Boolean = true,
                           val passwordValid: Boolean = true,
                           val error: Boolean = false,
                           val dismissToast: Boolean = false,
                           val signUpSuccess: Boolean = false)
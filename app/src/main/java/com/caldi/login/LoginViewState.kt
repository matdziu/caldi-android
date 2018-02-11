package com.caldi.login

data class LoginViewState(val inProgress: Boolean = false,
                          val emailValid: Boolean = true,
                          val passwordValid: Boolean = true,
                          val error: Boolean = false,
                          val dismissToast: Boolean = false,
                          val loginSuccess: Boolean = false)
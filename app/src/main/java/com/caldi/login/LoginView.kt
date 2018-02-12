package com.caldi.login

import io.reactivex.Observable

interface LoginView {

    fun render(loginViewState: LoginViewState)

    fun emitInput(): Observable<InputData>
}
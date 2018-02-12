package com.caldi.signup

import io.reactivex.Observable

interface SignUpView {

    fun render(signUpViewState: SignUpViewState)

    fun emitInput(): Observable<InputData>
}
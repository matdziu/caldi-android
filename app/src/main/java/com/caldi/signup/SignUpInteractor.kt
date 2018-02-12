package com.caldi.signup

import io.reactivex.Observable


class SignUpInteractor {

    fun createAccount(email: String, password: String): Observable<PartialSignUpViewState> {
        return Observable.just(PartialSignUpViewState.SignUpSuccess())
    }
}
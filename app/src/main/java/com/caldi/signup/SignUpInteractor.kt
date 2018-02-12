package com.caldi.signup

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit


class SignUpInteractor {

    fun createAccount(email: String, password: String): Observable<PartialSignUpViewState> {
        return if (email != "error") {
            Observable.timer(3000, TimeUnit.MILLISECONDS).map { PartialSignUpViewState.SignUpSuccess() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()) as Observable<PartialSignUpViewState>
        } else {
            Observable.timer(100, TimeUnit.MILLISECONDS).map { PartialSignUpViewState.ErrorState(true) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .startWith(PartialSignUpViewState.ErrorState()) as Observable<PartialSignUpViewState>
        }
    }
}
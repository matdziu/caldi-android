package com.caldi.login

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class LoginInteractor {

    fun login(email: String, password: String): Observable<PartialLoginViewState> {
        return if (email != "error") {
            Observable.timer(3000, TimeUnit.MILLISECONDS).map { PartialLoginViewState.LoginSuccess() }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io()) as Observable<PartialLoginViewState>
        } else {
            Observable.timer(100, TimeUnit.MILLISECONDS).map { PartialLoginViewState.ErrorState(true) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .startWith(PartialLoginViewState.ErrorState()) as Observable<PartialLoginViewState>
        }
    }
}
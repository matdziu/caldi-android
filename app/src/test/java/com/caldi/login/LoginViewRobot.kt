package com.caldi.login

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import junit.framework.Assert

class LoginViewRobot(loginViewModel: LoginViewModel) {

    private val renderedStates = arrayListOf<LoginViewState>()

    private val inputObservable: Subject<InputData> = PublishSubject.create()

    private val loginView = object : LoginView {

        override fun emitGoogleSignIn(): Observable<GoogleSignInAccount> = Completable.complete().toObservable()

        override fun render(loginViewState: LoginViewState) {
            renderedStates.add(loginViewState)
        }

        override fun emitInput(): Observable<InputData> = inputObservable

    }

    init {
        loginViewModel.bind(loginView)
    }

    fun clickLoginButton(email: String, password: String) {
        inputObservable.onNext(InputData(email, password))
    }

    fun assertViewStates(vararg expectedStates: LoginViewState) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}
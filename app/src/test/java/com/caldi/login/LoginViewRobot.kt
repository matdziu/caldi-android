package com.caldi.login

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import junit.framework.Assert

class LoginViewRobot(private val loginViewModel: LoginViewModel) {

    private val renderedStates = arrayListOf<LoginViewState>()

    private val inputObservable: Subject<InputData> = PublishSubject.create<InputData>()

    private val loginView = object : LoginView {

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
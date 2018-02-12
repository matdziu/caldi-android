package com.caldi.signup

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import junit.framework.Assert

class SignUpViewRobot(signUpViewModel: SignUpViewModel) {

    private val renderedStates = arrayListOf<SignUpViewState>()

    private val inputObservable: Subject<InputData> = PublishSubject.create()

    private val signUpView = object : SignUpView {

        override fun render(signUpViewState: SignUpViewState) {
            renderedStates.add(signUpViewState)
        }

        override fun emitInput(): Observable<InputData> = inputObservable
    }

    init {
        signUpViewModel.bind(signUpView)
    }

    fun clickCreateAccountButton(email: String, password: String, repeatPassword: String) {
        inputObservable.onNext(InputData(email, password, repeatPassword))
    }

    fun assertViewStates(vararg expectedStates: SignUpViewState) {
        Assert.assertEquals(expectedStates.toCollection(arrayListOf()), renderedStates)
    }
}
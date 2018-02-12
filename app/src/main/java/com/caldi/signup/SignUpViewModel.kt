package com.caldi.signup

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class SignUpViewModel(private val signUpInteractor: SignUpInteractor) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialSignUpViewState>()

    fun bind(signUpView: SignUpView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
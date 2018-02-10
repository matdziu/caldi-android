package com.caldi.login

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()
    private val stateSubject = BehaviorSubject.create<PartialLoginViewState>()

    fun bind(loginView: LoginView) {

    }

    fun unbind() {
        compositeDisposable.clear()
    }
}
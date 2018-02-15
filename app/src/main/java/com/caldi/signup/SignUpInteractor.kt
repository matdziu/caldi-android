package com.caldi.signup

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit


class SignUpInteractor {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun createAccount(email: String, password: String): Observable<PartialSignUpViewState> {
        val stateSubject: Subject<PartialSignUpViewState> = PublishSubject.create()
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        stateSubject.onNext(PartialSignUpViewState.SignUpSuccess())
                    } else {
                        Observable.timer(100, TimeUnit.MILLISECONDS)
                                .map { PartialSignUpViewState.ErrorState(true) }
                                .startWith(PartialSignUpViewState.ErrorState())
                                .subscribe(stateSubject)
                    }
                })
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }
}
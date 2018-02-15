package com.caldi.login

import com.google.firebase.auth.FirebaseAuth
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.concurrent.TimeUnit

class LoginInteractor {

    private val firebaseAuth = FirebaseAuth.getInstance()

    fun login(email: String, password: String): Observable<PartialLoginViewState> {
        val stateSubject: Subject<PartialLoginViewState> = PublishSubject.create()
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener({ task ->
                    if (task.isSuccessful) {
                        stateSubject.onNext(PartialLoginViewState.LoginSuccess())
                    } else {
                        Observable.timer(100, TimeUnit.MILLISECONDS)
                                .map { PartialLoginViewState.ErrorState(true) }
                                .startWith(PartialLoginViewState.ErrorState())
                                .subscribe(stateSubject)
                    }
                })
        return stateSubject.observeOn(AndroidSchedulers.mainThread())
    }

    fun isLoggedIn(): Observable<PartialLoginViewState.LoginSuccess> {
        return if (firebaseAuth.currentUser != null) {
            Observable.just(PartialLoginViewState.LoginSuccess())
        } else {
            Completable.complete().toObservable()
        }
    }
}
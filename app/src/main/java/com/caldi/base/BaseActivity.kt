package com.caldi.base

import android.support.v7.app.AppCompatActivity
import com.caldi.extensions.checkIfOnline
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject

open class BaseActivity : AppCompatActivity() {

    private lateinit var checkIfOnlineSubject: Subject<Boolean>
    private lateinit var checkIfOnlineDisposable: Disposable
    var online: Boolean = false

    override fun onStart() {
        super.onStart()
        checkIfOnlineSubject = checkIfOnline()
        checkIfOnlineDisposable = checkIfOnlineSubject.subscribe { online = it }
    }

    override fun onStop() {
        checkIfOnlineDisposable.dispose()
        super.onStop()
    }
}
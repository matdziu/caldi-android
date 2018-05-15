package com.caldi.base

import android.content.Intent
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.caldi.R
import com.caldi.constants.BLOCK_ALL_NOTIFICATIONS_KEY
import com.caldi.extensions.checkIfOnline
import com.caldi.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.Subject

open class BaseActivity : AppCompatActivity() {

    private lateinit var checkIfOnlineSubject: Subject<Boolean>
    private lateinit var checkIfOnlineDisposable: Disposable
    private var online: Boolean = false

    override fun onStart() {
        super.onStart()
        checkIfOnlineSubject = checkIfOnline()
        checkIfOnlineDisposable = checkIfOnlineSubject.subscribe { online = it }
    }

    override fun onStop() {
        checkIfOnlineDisposable.dispose()
        super.onStop()
    }

    protected fun signOut() {
        if (online) {
            disableAllNotifications()
            Thread { FirebaseInstanceId.getInstance().deleteInstanceId() }.start()
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.logout_connection_prompt), Toast.LENGTH_SHORT).show()
        }
    }

    private fun disableAllNotifications() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.edit()
                .putBoolean(BLOCK_ALL_NOTIFICATIONS_KEY, true)
                .apply()
    }

    protected fun enableAllNotifications() {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        sharedPref.edit()
                .putBoolean(BLOCK_ALL_NOTIFICATIONS_KEY, false)
                .apply()
    }
}
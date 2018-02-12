package com.caldi.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.factories.LoginViewModelFactory
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_login.contentViewGroup
import kotlinx.android.synthetic.main.activity_login.emailEditText
import kotlinx.android.synthetic.main.activity_login.loginButton
import kotlinx.android.synthetic.main.activity_login.passwordEditText
import kotlinx.android.synthetic.main.activity_login.progressBar
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var loginViewModelFactory: LoginViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this, loginViewModelFactory)[LoginViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.bind(this)
    }

    override fun onStop() {
        loginViewModel.unbind()
        super.onStop()
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            contentViewGroup.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        } else {
            contentViewGroup.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    override fun emitInput(): Observable<InputData> {
        return RxView.clicks(loginButton).map { InputData(emailEditText.text.toString(), passwordEditText.text.toString()) }
    }

    override fun render(loginViewState: LoginViewState) {
        showProgress(loginViewState.inProgress)
        emailEditText.showError(!loginViewState.emailValid)
        passwordEditText.showError(!loginViewState.passwordValid)

        if (loginViewState.error && !loginViewState.dismissToast) {
            Toast.makeText(this, getString(R.string.login_error_text), Toast.LENGTH_SHORT).show()
        }

        if (loginViewState.loginSuccess) {
            // go to app
        }
    }
}
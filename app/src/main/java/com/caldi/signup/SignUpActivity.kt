package com.caldi.signup

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.extensions.hideSoftKeyboard
import com.caldi.factories.SignUpViewModelFactory
import com.caldi.home.HomeActivity
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import kotlinx.android.synthetic.main.activity_sign_up.contentViewGroup
import kotlinx.android.synthetic.main.activity_sign_up.createAccountButton
import kotlinx.android.synthetic.main.activity_sign_up.emailEditText
import kotlinx.android.synthetic.main.activity_sign_up.passwordEditText
import kotlinx.android.synthetic.main.activity_sign_up.progressBar
import javax.inject.Inject

class SignUpActivity : AppCompatActivity(), SignUpView {

    private lateinit var signUpViewModel: SignUpViewModel

    @Inject
    lateinit var signUpViewModelFactory: SignUpViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        signUpViewModel = ViewModelProviders.of(this, signUpViewModelFactory)[SignUpViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        signUpViewModel.bind(this)
    }

    override fun onStop() {
        signUpViewModel.unbind()
        super.onStop()
    }

    override fun emitInput(): Observable<InputData> {
        return RxView.clicks(createAccountButton).map {
            InputData(emailEditText.text.toString(),
                    passwordEditText.text.toString())
        }
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

    override fun render(signUpViewState: SignUpViewState) {
        hideSoftKeyboard()
        showProgress(signUpViewState.inProgress)
        emailEditText.showError(!signUpViewState.emailValid)
        passwordEditText.showError(!signUpViewState.passwordValid)

        if (signUpViewState.error && !signUpViewState.dismissToast) {
            Toast.makeText(this, getString(signUpViewState.errorMessageId), Toast.LENGTH_SHORT).show()
        }

        if (signUpViewState.signUpSuccess) {
            val intent = Intent(this, HomeActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
    }
}
package com.caldi.login

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.caldi.R
import com.caldi.extensions.hideSoftKeyboard
import com.caldi.factories.LoginViewModelFactory
import com.caldi.home.HomeActivity
import com.caldi.signup.SignUpActivity
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes.SIGN_IN_FAILED
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes.NETWORK_ERROR
import com.jakewharton.rxbinding2.view.RxView
import dagger.android.AndroidInjection
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import kotlinx.android.synthetic.main.activity_login.contentViewGroup
import kotlinx.android.synthetic.main.activity_login.createAccountButton
import kotlinx.android.synthetic.main.activity_login.emailEditText
import kotlinx.android.synthetic.main.activity_login.loginButton
import kotlinx.android.synthetic.main.activity_login.loginWithFacebookButton
import kotlinx.android.synthetic.main.activity_login.loginWithGoogleButton
import kotlinx.android.synthetic.main.activity_login.passwordEditText
import kotlinx.android.synthetic.main.activity_login.progressBar
import javax.inject.Inject


class LoginActivity : AppCompatActivity(), LoginView {

    private val requestCodeSignIn = 1
    private val googleSignInObservable: Subject<GoogleSignInAccount> = PublishSubject.create()
    private val facebookSignInObservable: Subject<AccessToken> = PublishSubject.create()

    private lateinit var loginViewModel: LoginViewModel

    @Inject
    lateinit var loginViewModelFactory: LoginViewModelFactory

    @Inject
    lateinit var googleSignInClient: GoogleSignInClient

    @Inject
    lateinit var callbackManager: CallbackManager

    @Inject
    lateinit var loginManager: LoginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this, loginViewModelFactory)[LoginViewModel::class.java]

        initFacebookSignInCallback()

        createAccountButton.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
        loginWithGoogleButton.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, requestCodeSignIn)
        }
        loginWithFacebookButton.setOnClickListener {
            loginManager.logInWithReadPermissions(this, arrayListOf("email"))
        }
    }

    override fun onStart() {
        super.onStart()
        loginViewModel.bind(this)
    }

    override fun onStop() {
        loginViewModel.unbind()
        super.onStop()
    }

    private fun initFacebookSignInCallback() {
        loginManager.registerCallback(callbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(loginResult: LoginResult) {
                        facebookSignInObservable.onNext(loginResult.accessToken)
                    }

                    override fun onCancel() {
                        Log.e("FacebookSignIn", "onCancel()")
                    }

                    override fun onError(exception: FacebookException) {
                        Toast.makeText(this@LoginActivity, getString(R.string.login_error_text), Toast.LENGTH_SHORT).show()
                    }
                })
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodeSignIn) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                googleSignInObservable.onNext(account)
            } catch (exception: ApiException) {
                if (exception.statusCode == SIGN_IN_FAILED || exception.statusCode == NETWORK_ERROR) {
                    Toast.makeText(this, getString(R.string.login_error_text), Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun emitGoogleSignIn(): Observable<GoogleSignInAccount> = googleSignInObservable

    override fun emitFacebookSignIn(): Observable<AccessToken> = facebookSignInObservable

    override fun emitInput(): Observable<InputData> {
        return RxView.clicks(loginButton).map { InputData(emailEditText.text.toString(), passwordEditText.text.toString()) }
    }

    override fun render(loginViewState: LoginViewState) {
        hideSoftKeyboard()
        showProgress(loginViewState.inProgress)
        emailEditText.showError(!loginViewState.emailValid)
        passwordEditText.showError(!loginViewState.passwordValid)

        if (loginViewState.error && !loginViewState.dismissToast) {
            Toast.makeText(this, getString(R.string.login_error_text), Toast.LENGTH_SHORT).show()
        }

        if (loginViewState.loginSuccess) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
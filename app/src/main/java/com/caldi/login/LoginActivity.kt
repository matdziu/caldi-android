package com.caldi.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.caldi.R
import com.caldi.factories.LoginViewModelFactory
import dagger.android.AndroidInjection
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

    override fun render(loginViewState: LoginViewState) {

    }
}
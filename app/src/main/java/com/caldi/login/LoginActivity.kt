package com.caldi.login

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.caldi.R
import com.caldi.factories.LoginViewModelFactory

class LoginActivity : AppCompatActivity(), LoginView {

    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        loginViewModel = ViewModelProviders.of(this, LoginViewModelFactory(LoginRepository()))[LoginViewModel::class.java]
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
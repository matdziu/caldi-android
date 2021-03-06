package com.caldi.login

import com.caldi.R
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test

class LoginViewModelTest {

    private val loginInteractor: LoginInteractor = mock()
    private val loginViewModel = LoginViewModel(loginInteractor)

    @Before
    fun setUp() {
        whenever(loginInteractor.isLoggedIn()).thenReturn(Completable.complete().toObservable())
    }

    @Test
    fun testWithCorrectInput() {
        val loginViewRobot = LoginViewRobot(loginViewModel)
        whenever(loginInteractor.login(any(), any())).thenReturn(Observable.just(PartialLoginViewState.LoginSuccess()))

        loginViewRobot.clickLoginButton("test@test.com", "qwerty")

        loginViewRobot.assertViewStates(LoginViewState(),
                LoginViewState(inProgress = true),
                LoginViewState(loginSuccess = true))
    }

    @Test
    fun testWithEmptyInput() {
        val loginViewRobot = LoginViewRobot(loginViewModel)

        loginViewRobot.clickLoginButton("", "   \n")
        loginViewRobot.clickLoginButton("test@test", "   \n")
        loginViewRobot.clickLoginButton("", "qwerty")

        loginViewRobot.assertViewStates(LoginViewState(),
                LoginViewState(emailValid = false, passwordValid = false),
                LoginViewState(emailValid = true, passwordValid = false),
                LoginViewState(emailValid = false, passwordValid = true))
    }

    @Test
    fun testWithErrorFromInteractor() {
        val loginViewRobot = LoginViewRobot(loginViewModel)
        whenever(loginInteractor.login(any(), any())).thenReturn(Observable.just(
                PartialLoginViewState.ErrorState(null)))

        loginViewRobot.clickLoginButton("test@test.com", "qwerty")

        loginViewRobot.assertViewStates(
                LoginViewState(),
                LoginViewState(
                        inProgress = true),
                LoginViewState(
                        errorMessageId = R.string.generic_error,
                        error = true))
    }
}
package com.caldi.signup

import com.caldi.R
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Observable
import org.junit.Test

class SignUpViewModelTest {

    private val signUpInteractor: SignUpInteractor = mock()
    private val signUpViewModel = SignUpViewModel(signUpInteractor)

    @Test
    fun testWithCorrectInput() {
        val signUpViewRobot = SignUpViewRobot(signUpViewModel)
        whenever(signUpInteractor.createAccount(any(), any())).thenReturn(Observable.just(PartialSignUpViewState.SignUpSuccess()))

        signUpViewRobot.clickCreateAccountButton("test@test.com", "qwerty")

        signUpViewRobot.assertViewStates(SignUpViewState(),
                SignUpViewState(inProgress = true),
                SignUpViewState(signUpSuccess = true))
    }

    @Test
    fun testWithEmptyInput() {
        val signUpViewRobot = SignUpViewRobot(signUpViewModel)

        signUpViewRobot.clickCreateAccountButton(" ", "\n")
        signUpViewRobot.clickCreateAccountButton("test@test", "\n")

        signUpViewRobot.assertViewStates(SignUpViewState(),
                SignUpViewState(emailValid = false, passwordValid = false),
                SignUpViewState(passwordValid = false))
    }

    @Test
    fun testWithErrorFromInteractor() {
        val signUpViewRobot = SignUpViewRobot(signUpViewModel)
        whenever(signUpInteractor.createAccount(any(), any())).thenReturn(
                Observable.just(PartialSignUpViewState.ErrorState(null)))

        signUpViewRobot.clickCreateAccountButton("test@test.com", "qwerty")

        signUpViewRobot.assertViewStates(
                SignUpViewState(),
                SignUpViewState(
                        inProgress = true),
                SignUpViewState(
                        errorMessageId = R.string.generic_error,
                        error = true))
    }
}
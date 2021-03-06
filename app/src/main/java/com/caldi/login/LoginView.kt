package com.caldi.login

import com.facebook.AccessToken
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import io.reactivex.Observable

interface LoginView {

    fun render(loginViewState: LoginViewState)

    fun emitInput(): Observable<InputData>

    fun emitGoogleSignIn(): Observable<GoogleSignInAccount>

    fun emitFacebookSignIn(): Observable<AccessToken>
}
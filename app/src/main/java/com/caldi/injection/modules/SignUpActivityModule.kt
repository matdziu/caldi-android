package com.caldi.injection.modules

import com.caldi.factories.SignUpViewModelFactory
import com.caldi.injection.ActivityScope
import com.caldi.signup.SignUpInteractor
import dagger.Module
import dagger.Provides

@Module
class SignUpActivityModule {

    @Provides
    @ActivityScope
    fun provideSignUpViewModelFactory(signUpInteractor: SignUpInteractor): SignUpViewModelFactory {
        return SignUpViewModelFactory(signUpInteractor)
    }

    @Provides
    @ActivityScope
    fun provideSignUpInteractor(): SignUpInteractor {
        return SignUpInteractor()
    }
}
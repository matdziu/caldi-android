package com.caldi.injection.modules

import com.caldi.factories.LoginViewModelFactory
import com.caldi.injection.ActivityScope
import com.caldi.login.LoginInteractor
import dagger.Module
import dagger.Provides

@Module
class LoginActivityModule {

    @Provides
    @ActivityScope
    fun provideLoginViewModelFactory(loginInteractor: LoginInteractor): LoginViewModelFactory {
        return LoginViewModelFactory(loginInteractor)
    }

    @Provides
    @ActivityScope
    fun provideLoginRepository(): LoginInteractor {
        return LoginInteractor()
    }
}
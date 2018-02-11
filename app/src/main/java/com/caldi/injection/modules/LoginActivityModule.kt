package com.caldi.injection.modules

import com.caldi.factories.LoginViewModelFactory
import com.caldi.injection.ActivityScope
import com.caldi.login.LoginRepository
import dagger.Module
import dagger.Provides

@Module
class LoginActivityModule {

    @Provides
    @ActivityScope
    fun provideLoginViewModelFactory(loginRepository: LoginRepository): LoginViewModelFactory {
        return LoginViewModelFactory(loginRepository)
    }

    @Provides
    @ActivityScope
    fun provideLoginRepository(): LoginRepository {
        return LoginRepository()
    }
}
package com.caldi.injection.modules

import com.caldi.factories.LoginViewModelFactory
import com.caldi.login.LoginRepository
import dagger.Module
import dagger.Provides

@Module
class LoginActivityModule {

    @Provides
    fun provideLoginViewModelFactory(loginRepository: LoginRepository): LoginViewModelFactory {
        return LoginViewModelFactory(loginRepository)
    }

    @Provides
    fun provideLoginRepository(): LoginRepository {
        return LoginRepository()
    }
}
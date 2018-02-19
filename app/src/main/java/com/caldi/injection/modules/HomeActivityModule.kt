package com.caldi.injection.modules

import com.caldi.factories.HomeViewModelFactory
import com.caldi.home.HomeInteractor
import com.caldi.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class HomeActivityModule {

    @Provides
    @ActivityScope
    fun provideHomeViewModelFactory(homeInteractor: HomeInteractor): HomeViewModelFactory {
        return HomeViewModelFactory(homeInteractor)
    }

    @Provides
    @ActivityScope
    fun provideHomeInteractor(): HomeInteractor {
        return HomeInteractor()
    }
}
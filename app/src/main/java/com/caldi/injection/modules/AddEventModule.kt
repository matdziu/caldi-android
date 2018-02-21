package com.caldi.injection.modules

import com.caldi.addevent.AddEventInteractor
import com.caldi.factories.AddEventViewModelFactory
import com.caldi.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class AddEventModule {

    @Provides
    @ActivityScope
    fun provideAddEventViewModelFactory(addEventInteractor: AddEventInteractor): AddEventViewModelFactory {
        return AddEventViewModelFactory(addEventInteractor)
    }

    @Provides
    @ActivityScope
    fun provideAddEventInteractor(): AddEventInteractor {
        return AddEventInteractor()
    }
}
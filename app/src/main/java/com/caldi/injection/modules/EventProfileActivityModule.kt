package com.caldi.injection.modules

import com.caldi.eventprofile.EventProfileInteractor
import com.caldi.factories.EventProfileViewModelFactory
import com.caldi.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class EventProfileActivityModule {

    @Provides
    @ActivityScope
    fun provideEventProfileViewModelFactory(eventProfileInteractor: EventProfileInteractor)
            : EventProfileViewModelFactory {
        return EventProfileViewModelFactory(eventProfileInteractor)
    }

    @Provides
    @ActivityScope
    fun provideEventProfileInteractor(): EventProfileInteractor {
        return EventProfileInteractor()
    }
}
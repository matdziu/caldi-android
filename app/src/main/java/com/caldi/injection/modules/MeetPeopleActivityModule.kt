package com.caldi.injection.modules

import com.caldi.factories.MeetPeopleViewModelFactory
import com.caldi.injection.ActivityScope
import com.caldi.meetpeople.MeetPeopleInteractor
import dagger.Module
import dagger.Provides

@Module
class MeetPeopleActivityModule {

    @Provides
    @ActivityScope
    fun provideMeetPeopleViewModelFactory(meetPeopleInteractor: MeetPeopleInteractor):
            MeetPeopleViewModelFactory {
        return MeetPeopleViewModelFactory(meetPeopleInteractor)
    }

    @Provides
    @ActivityScope
    fun provideMeetPeopleInteractor(): MeetPeopleInteractor {
        return MeetPeopleInteractor()
    }
}
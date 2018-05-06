package com.caldi.injection.modules

import com.caldi.factories.PeopleViewModelFactory
import com.caldi.injection.ActivityScope
import com.caldi.people.common.PeopleInteractor
import dagger.Module
import dagger.Provides

@Module
class PeopleModule {

    @Provides
    @ActivityScope
    fun provideMeetPeopleViewModelFactory(peopleInteractor: PeopleInteractor):
            PeopleViewModelFactory {
        return PeopleViewModelFactory(peopleInteractor)
    }

    @Provides
    @ActivityScope
    fun provideMeetPeopleInteractor(): PeopleInteractor {
        return PeopleInteractor()
    }
}
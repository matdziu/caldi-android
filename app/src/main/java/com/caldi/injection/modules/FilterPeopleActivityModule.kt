package com.caldi.injection.modules

import com.caldi.factories.FilterPeopleViewModelFactory
import com.caldi.filterpeople.FilterPeopleInteractor
import com.caldi.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class FilterPeopleActivityModule {

    @Provides
    @ActivityScope
    fun provideFilterPeopleViewModelFactory(filterPeopleInteractor: FilterPeopleInteractor)
            : FilterPeopleViewModelFactory {
        return FilterPeopleViewModelFactory(filterPeopleInteractor)
    }

    @Provides
    @ActivityScope
    fun provideFilterPeopleInteractor(): FilterPeopleInteractor {
        return FilterPeopleInteractor()
    }
}
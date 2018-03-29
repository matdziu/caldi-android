package com.caldi.injection.modules

import com.caldi.factories.OrganizerViewModelFactory
import com.caldi.injection.ActivityScope
import com.caldi.organizer.OrganizerInteractor
import dagger.Module
import dagger.Provides

@Module
class OrganizerActivityModule {

    @Provides
    @ActivityScope
    fun provideOrganizerViewModelFactory(organizerInteractor: OrganizerInteractor): OrganizerViewModelFactory {
        return OrganizerViewModelFactory(organizerInteractor)
    }

    @Provides
    @ActivityScope
    fun provideOrganizerInteractor(): OrganizerInteractor {
        return OrganizerInteractor()
    }
}
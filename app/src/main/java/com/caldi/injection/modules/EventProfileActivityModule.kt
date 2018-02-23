package com.caldi.injection.modules

import com.caldi.eventprofile.EventProfileInteractor
import com.caldi.eventprofile.list.QuestionsAdapter
import com.caldi.eventprofile.list.QuestionsViewModel
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

    @Provides
    @ActivityScope
    fun provideQuestionsViewModel(): QuestionsViewModel {
        return QuestionsViewModel()
    }

    @Provides
    @ActivityScope
    fun provideQuestionsAdapter(questionsViewModel: QuestionsViewModel): QuestionsAdapter {
        return QuestionsAdapter(questionsViewModel)
    }
}
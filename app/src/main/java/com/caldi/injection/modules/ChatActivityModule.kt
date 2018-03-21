package com.caldi.injection.modules

import com.caldi.chat.ChatInteractor
import com.caldi.factories.ChatViewModelFactory
import com.caldi.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class ChatActivityModule {

    @Provides
    @ActivityScope
    fun provideChatViewModelFactory(chatInteractor: ChatInteractor): ChatViewModelFactory {
        return ChatViewModelFactory(chatInteractor)
    }

    @Provides
    @ActivityScope
    fun provideChatInteractor(): ChatInteractor {
        return ChatInteractor()
    }
}
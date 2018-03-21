package com.caldi.injection.modules

import com.caldi.chatlist.ChatListInteractor
import com.caldi.factories.ChatListViewModelFactory
import com.caldi.injection.ActivityScope
import dagger.Module
import dagger.Provides

@Module
class ChatListActivityModule {

    @Provides
    @ActivityScope
    fun provideChatListViewModelFactory(chatListInteractor: ChatListInteractor): ChatListViewModelFactory {
        return ChatListViewModelFactory(chatListInteractor)
    }

    @Provides
    @ActivityScope
    fun provideChatListInteractor(): ChatListInteractor {
        return ChatListInteractor()
    }
}
package com.caldi.injection.modules

import com.caldi.addevent.AddEventActivity
import com.caldi.chat.ChatActivity
import com.caldi.chatlist.ChatListActivity
import com.caldi.eventprofile.EventProfileActivity
import com.caldi.home.HomeActivity
import com.caldi.injection.ActivityScope
import com.caldi.login.LoginActivity
import com.caldi.organizer.OrganizerActivity
import com.caldi.people.filterpeople.FilterPeopleActivity
import com.caldi.people.meetpeople.MeetPeopleActivity
import com.caldi.signup.SignUpActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ActivityBuilder {

    @ActivityScope
    @ContributesAndroidInjector(modules = [LoginActivityModule::class, GoogleSignInModule::class,
        FacebookSignInModule::class])
    abstract fun bindLoginActivity(): LoginActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [SignUpActivityModule::class])
    abstract fun bindSignUpActivity(): SignUpActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [HomeActivityModule::class])
    abstract fun bindHomeActivity(): HomeActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [AddEventModule::class])
    abstract fun bindAddEventActivity(): AddEventActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [EventProfileActivityModule::class])
    abstract fun bindEventProfileActivity(): EventProfileActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PeopleModule::class])
    abstract fun bindMeetPeopleActivity(): MeetPeopleActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [PeopleModule::class])
    abstract fun bindFilterPeopleActivity(): FilterPeopleActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ChatListActivityModule::class])
    abstract fun bindChatListActivity(): ChatListActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [ChatActivityModule::class])
    abstract fun bindChatActivity(): ChatActivity

    @ActivityScope
    @ContributesAndroidInjector(modules = [OrganizerActivityModule::class])
    abstract fun bindOrganizerActivity(): OrganizerActivity
}
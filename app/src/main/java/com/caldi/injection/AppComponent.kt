package com.caldi.injection

import com.caldi.CaldiApplication
import com.caldi.injection.modules.ActivityBuilder
import com.caldi.injection.modules.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton


@Component(modules = [
    AndroidInjectionModule::class,
    ActivityBuilder::class,
    AppModule::class])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(caldiApplication: CaldiApplication): Builder

        fun build(): AppComponent
    }

    fun inject(caldiApplication: CaldiApplication)
}
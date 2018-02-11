package com.caldi.injection.modules

import android.content.Context
import com.caldi.CaldiApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideContext(caldiApplication: CaldiApplication): Context {
        return caldiApplication
    }
}
package com.caldi

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import com.caldi.injection.DaggerAppComponent
import com.caldi.notifications.utils.NotificationChannelsBuilder
import com.crashlytics.android.Crashlytics
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import io.fabric.sdk.android.Fabric
import javax.inject.Inject


class CaldiApplication : Application(), HasActivityInjector {

    var visibleActivity: Activity? = null

    @Inject
    lateinit var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelsBuilder(this).buildChannels()
        }

        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this)

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            override fun onActivityStarted(activity: Activity?) {
                visibleActivity = activity
            }

            override fun onActivityStopped(activity: Activity?) {
                if (activity == visibleActivity) visibleActivity = null
            }

            override fun onActivityPaused(activity: Activity?) {
                // unused
            }

            override fun onActivityResumed(activity: Activity?) {
                // unused
            }

            override fun onActivityDestroyed(activity: Activity?) {
                // unused
            }

            override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
                // unused
            }

            override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
                // unused
            }
        })
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityDispatchingAndroidInjector
    }
}
package com.example.historyquiz.ui.base

import android.app.Activity
import android.app.Application
import com.example.historyquiz.di.components.AppComponent
import com.example.historyquiz.di.components.DaggerAppComponent
import com.google.firebase.FirebaseApp
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()


        sAppComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        sAppComponent.inject(this)

    }

    override fun activityInjector(): AndroidInjector<Activity>? {
        return dispatchingAndroidInjector
    }

    companion object {
        lateinit var sAppComponent: AppComponent
    }
}
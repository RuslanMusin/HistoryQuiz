package com.example.historyquiz.ui.base

import com.example.historyquiz.di.components.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

class CastleApplication : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).create(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
/*
class App : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<android.support.v7.app.AppCompatActivity>

    override fun onCreate() {
        super.onCreate()


        sAppComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()

        sAppComponent.inject(this)

    }

    override fun activityInjector(): AndroidInjector<android.support.v7.app.AppCompatActivity>? {
        return dispatchingAndroidInjector
    }

    companion object {
        lateinit var sAppComponent: AppComponent
    }
}*/

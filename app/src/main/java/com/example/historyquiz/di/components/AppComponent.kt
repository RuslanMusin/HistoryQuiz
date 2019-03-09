package com.example.historyquiz.di.components

import android.content.Context
import com.example.historyquiz.di.modules.AppModule
import com.example.historyquiz.ui.auth.fragments.login.LoginFragmentPresenter
import com.example.historyquiz.ui.auth.fragments.signup.SignUpFragment
import com.example.historyquiz.ui.auth.fragments.signup.SignUpPresenter
import com.example.historyquiz.ui.base.App
import com.example.historyquiz.ui.profile.item.ProfilePresenter
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: App)

    fun inject(presenter: LoginFragmentPresenter)

    fun inject(presenter: SignUpPresenter)

    fun inject(presenter: ProfilePresenter)

}
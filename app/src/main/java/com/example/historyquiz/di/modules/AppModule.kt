package com.example.historyquiz.di.modules
import com.example.historyquiz.ui.auth.LoginActivity
import com.example.historyquiz.ui.auth.fragments.login.LoginFragment
import com.example.historyquiz.ui.auth.fragments.signup.SignUpFragment
import com.example.historyquiz.ui.navigation.NavigationActivity
import com.example.historyquiz.ui.profile.item.ProfileFragment
import com.example.studentapp.di.scopes.ActivityScope
import com.example.studentapp.di.scopes.FragmentScope
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Module(includes = [AndroidSupportInjectionModule::class, FirebaseModule::class])
interface AppModule {

    @ActivityScope
    @ContributesAndroidInjector()
    fun loginActivityInjector(): LoginActivity

    @FragmentScope
    @ContributesAndroidInjector()
    fun loginFragmentInjector(): LoginFragment

    @FragmentScope
    @ContributesAndroidInjector()
    fun signUpFragmentInjector(): SignUpFragment

    @ActivityScope
    @ContributesAndroidInjector()
    fun navigationActivityInjector(): NavigationActivity

    @FragmentScope
    @ContributesAndroidInjector()
    fun profileFragmentInjector(): ProfileFragment
}

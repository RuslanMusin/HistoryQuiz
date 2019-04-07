package com.example.historyquiz.di.modules

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module()
public class FirebaseModule {

    @Provides
    @Singleton
    fun provideFireAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}
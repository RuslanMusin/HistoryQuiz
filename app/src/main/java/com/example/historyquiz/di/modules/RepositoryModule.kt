package com.example.historyquiz.di.modules

import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.repository.api.WikiApiRepositoryImpl
import com.example.historyquiz.repository.auth.AuthRepository
import com.example.historyquiz.repository.auth.AuthRepositoryImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [ServiceModule::class])
interface RepositoryModule {

    @Singleton
    @Binds
    fun authRepository(repository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun wikiApiRepository(repository: WikiApiRepositoryImpl): WikiApiRepository

}
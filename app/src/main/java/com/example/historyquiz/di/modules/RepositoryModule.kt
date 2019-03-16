package com.example.historyquiz.di.modules

import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.repository.api.WikiApiRepositoryImpl
import com.example.historyquiz.repository.auth.AuthRepository
import com.example.historyquiz.repository.auth.AuthRepositoryImpl
import com.example.historyquiz.repository.card.AbstractCardRepository
import com.example.historyquiz.repository.card.AbstractCardRepositoryImpl
import com.example.historyquiz.repository.card.CardRepository
import com.example.historyquiz.repository.card.CardRepositoryImpl
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.repository.test.TestRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module(includes = [ServiceModule::class])
interface RepositoryModule {

    @Singleton
    @Binds
    fun authRepository(repository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun wikiApiRepository(repository: WikiApiRepositoryImpl): WikiApiRepository

    @Singleton
    @Binds
    fun abstractCardRepository(repository: AbstractCardRepositoryImpl): AbstractCardRepository

    @Singleton
    @Binds
    fun cardRepository(repository: CardRepositoryImpl): CardRepository

    @Singleton
    @Binds
    fun testRepository(repository: TestRepositoryImpl): TestRepository

}
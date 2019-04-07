package com.example.historyquiz.di.modules

import com.example.historyquiz.repository.api.WikiApiRepository
import com.example.historyquiz.repository.api.WikiApiRepositoryImpl
import com.example.historyquiz.repository.auth.AuthRepository
import com.example.historyquiz.repository.auth.AuthRepositoryImpl
import com.example.historyquiz.repository.card.*
import com.example.historyquiz.repository.epoch.*
import com.example.historyquiz.repository.game.GameRepository
import com.example.historyquiz.repository.game.GameRepositoryImpl
import com.example.historyquiz.repository.test.TestRepository
import com.example.historyquiz.repository.test.TestRepositoryImpl
import com.example.historyquiz.repository.user.UserRepository
import com.example.historyquiz.repository.user.UserRepositoryImpl
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

    @Singleton
    @Binds
    fun abstractCardRepository(repository: AbstractCardRepositoryImpl): AbstractCardRepository

    @Singleton
    @Binds
    fun cardRepository(repository: CardRepositoryImpl): CardRepository

    @Singleton
    @Binds
    fun testRepository(repository: TestRepositoryImpl): TestRepository

    @Singleton
    @Binds
    fun commentRepository(repository: CommentRepositoryImpl): CommentRepository

    @Singleton
    @Binds
    fun userRepository(repository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    fun epochRepository(repository: EpochRepositoryImpl): EpochRepository

    @Singleton
    @Binds
    fun userEpochRepository(repository: UserEpochRepositoryImpl): UserEpochRepository

    @Singleton
    @Binds
    fun gameRepository(repository: GameRepositoryImpl): GameRepository

    @Singleton
    @Binds
    fun leaderStatRepository(repository: LeaderStatRepositoryImpl): LeaderStatRepository

}
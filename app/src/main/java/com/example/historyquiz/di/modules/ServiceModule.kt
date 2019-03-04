package com.example.historyquiz.di.modules

import com.example.historyquiz.api.services.AuthService
import com.example.historyquiz.api.services.WikiService
import com.example.historyquiz.di.modules.RetrofitModule
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [RetrofitModule::class])
public class ServiceModule {

    @Provides
    @Singleton
    fun provideAuthService(
        @Named("auth") retrofit: Retrofit
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Provides
    @Singleton
    fun provideWikiService(
        @Named("main") retrofit: Retrofit
    ): WikiService {
        return retrofit.create(WikiService::class.java)
    }

}

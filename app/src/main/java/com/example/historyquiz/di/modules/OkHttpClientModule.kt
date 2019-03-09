package com.example.historyquiz.di.modules

import com.example.historyquiz.api.interceptors.ApiKeyInterceptor
import com.example.historyquiz.api.interceptors.ErrorHandlingInterceptor
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@Module
class OkHttpClientModule {

    @Provides
    @Singleton
    @Named("auth")
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    @Named("main")
    fun provideMainHttpClient(@Named("auth") okHttpClient: OkHttpClient): OkHttpClient {
        return okHttpClient
            .newBuilder()
            .addInterceptor(ApiKeyInterceptor.create())
            .addInterceptor(ErrorHandlingInterceptor.create())
            .build()
    }
}
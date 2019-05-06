package com.example.historyquiz.di.modules

import com.example.historyquiz.api.services.WikiService
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [RetrofitModule::class])
public class ServiceModule {

    @Provides
    @Singleton
    fun provideWikiService(
        @Named("main") retrofit: Retrofit
    ): WikiService {
        return retrofit.create(WikiService::class.java)
    }

}

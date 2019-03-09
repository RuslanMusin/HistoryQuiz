package com.example.historyquiz.di.modules

import com.example.historyquiz.utils.Const.BASE_URL
import com.example.historyquiz.utils.Const.TIME_FORMAT
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module(includes = [OkHttpClientModule::class])
public class RetrofitModule {

    @Provides
    @Singleton
    @Named("main")
    fun provideMainRetrofit(
        @Named("main") okHttpClient: OkHttpClient,
        builder: Retrofit.Builder
    ): Retrofit {
        return builder
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(
        @Named("auth") okHttpClient: OkHttpClient,
        builder: Retrofit.Builder
    ): Retrofit {
        return builder
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofitBuilder(converterFactory: SimpleXmlConverterFactory): Retrofit.Builder {
        return Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(converterFactory)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat(TIME_FORMAT)
            .create()
    }

    @Provides
    @Singleton
    fun provideConverterFactory(): SimpleXmlConverterFactory {
        return SimpleXmlConverterFactory.create()
    }

/* @Provides
 @Singleton
 fun provideUtils(): ErrorUtils {
     return ErrorUtils
 }*/

}
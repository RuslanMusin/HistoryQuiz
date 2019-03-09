package com.example.historyquiz.api

import okhttp3.OkHttpClient

object OkHttpProvider {

    private val baseClient = OkHttpClient()

    @Volatile
    private var mainClient: OkHttpClient? = null

    @Volatile
    private var loginClient: OkHttpClient? = null

    /* fun provideClient(): OkHttpClient? {
         var client = mainClient
         if (client == null) {
            synchronized()
         }
     }*/
}
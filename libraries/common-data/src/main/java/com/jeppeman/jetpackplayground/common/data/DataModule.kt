package com.jeppeman.jetpackplayground.common.data

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
object DataModule {
    @Provides
    @JvmStatic
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.create()
}
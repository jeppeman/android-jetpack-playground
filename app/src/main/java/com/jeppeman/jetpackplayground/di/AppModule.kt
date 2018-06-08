package com.jeppeman.jetpackplayground.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.createFeatureManager
import com.jeppeman.jetpackplayground.MainApplication
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
object AppModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideContext(application: MainApplication): Context = application

    @Provides
    @Singleton
    @JvmStatic
    fun provideMainThreadHandler(): Handler = Handler(Looper.getMainLooper())

    @Provides
    @Singleton
    @JvmStatic
    fun provideBackgroundDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @JvmStatic
    @Singleton
    fun provideFeatureManager(context: Context): FeatureManager = createFeatureManager(context)

    @Provides
    @JvmStatic
    @Singleton
    fun provideHomeFeatureDependencies(context: Context): HomeFeature.Dependencies =
            object : HomeFeature.Dependencies {
                override val context: Context = context
            }

    @Provides
    @JvmStatic
    @Singleton
    fun provideVideoFeatureDependencies(
            context: Context,
            okHttpClient: OkHttpClient,
            handler: Handler,
            backgroundDispatcher: CoroutineDispatcher
    ): VideoFeature.Dependencies =
            object : VideoFeature.Dependencies {
                override val okHttpClient: OkHttpClient = okHttpClient
                override val context: Context = context
                override val handler: Handler = handler
                override val backgroundDispatcher: CoroutineDispatcher = backgroundDispatcher
            }
}
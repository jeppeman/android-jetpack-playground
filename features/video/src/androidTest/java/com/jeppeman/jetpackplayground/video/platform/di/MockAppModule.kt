package com.jeppeman.jetpackplayground.video.platform.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.test.espresso.IdlingRegistry
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.jeppeman.jetpackplayground.common.data.create
import com.jeppeman.jetpackplayground.common_features.FeatureManager
import com.jeppeman.jetpackplayground.common_features.HomeFeature
import com.jeppeman.jetpackplayground.common_features.VideoFeature
import com.jeppeman.jetpackplayground.common_features.createFeatureManager
import com.jeppeman.jetpackplayground.video.MockApplication
import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
object MockAppModule {
    @Provides
    @Singleton
    @JvmStatic
    fun provideContext(application: MockApplication): Context = application

    @Provides
    @Singleton
    @JvmStatic
    fun provideMainThreadHandler(): Handler = Handler(Looper.getMainLooper())

    @Provides
    @Singleton
    @JvmStatic
    fun provideBackgroundDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @Singleton
    @JvmStatic
    fun provideHttpClient(): OkHttpClient =
            OkHttpClient.create()
                    .newBuilder()
                    .addInterceptor { chain ->
                        val mockWebServerUrl = MockApplication.mockWebServer.url("/")
                        chain.proceed(
                                chain.request().newBuilder().url(
                                        chain.request().url.newBuilder()
                                                .scheme(mockWebServerUrl.scheme)
                                                .host(mockWebServerUrl.host)
                                                .port(mockWebServerUrl.port)
                                                .build()
                                ).build()
                        )
                    }
                    .build()
                    .apply {
                        IdlingRegistry.getInstance().register(
                                OkHttp3IdlingResource.create("OkHttp", this)
                        )
                    }

    @Provides
    @JvmStatic
    @Singleton
    fun provideFeatureManager(context: Context): FeatureManager = createFeatureManager(context)

    @Provides
    @JvmStatic
    @Singleton
    fun provideHomeFeatureDependencies(context: Context, featureManager: FeatureManager): HomeFeature.Dependencies =
            object : HomeFeature.Dependencies {
                override val context: Context = context
                override val featureManager: FeatureManager = featureManager
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
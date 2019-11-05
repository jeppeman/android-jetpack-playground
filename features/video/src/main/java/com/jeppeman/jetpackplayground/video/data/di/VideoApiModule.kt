package com.jeppeman.jetpackplayground.video.data.di

import android.content.Context
import com.jeppeman.jetpackplayground.video.R
import com.jeppeman.jetpackplayground.video.data.net.VideoApi
import com.jeppeman.jetpackplayground.video.platform.di.VideoScope
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

@Module
object VideoApiModule {
    @JvmStatic
    @Provides
    @VideoScope
    fun provideVideoApiAsync(
            @VideoApiBaseUrl videoApiBaseUrl: String,
            moshi: Moshi,
            okHttpClient: OkHttpClient
    ): VideoApi {
        return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(videoApiBaseUrl)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(VideoApi::class.java)
    }

    @JvmStatic
    @Provides
    @VideoScope
    @VideoApiBaseUrl
    fun provideVideoApiBaseUrl(context: Context): String {
        return context.getString(R.string.video_api_base_url)
    }

    @JvmStatic
    @Provides
    @VideoScope
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
    }
}
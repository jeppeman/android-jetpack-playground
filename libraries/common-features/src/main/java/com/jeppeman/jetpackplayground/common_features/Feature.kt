package com.jeppeman.jetpackplayground.common_features

import android.content.Context
import android.os.Handler
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import kotlin.reflect.KClass

internal fun <T : Feature<*>> KClass<T>.info(context: Context) = when (this) {
    HomeFeature::class -> Feature.Info(
            id = "home",
            name = context.getString(R.string.title_feature_home),
            actionId = R.id.actionHome
    )
    VideoFeature::class -> Feature.Info(
            id = "video",
            name = context.getString(R.string.title_feature_video),
            actionId = R.id.actionVideo
    )
    else -> throw IllegalArgumentException("Unexpected feature $this")
}

interface Feature<T> {
    fun getEntryPoint(): Fragment
    fun inject(dependencies: T)

    data class Info(
            val id: String,
            val name: String,
            @IdRes val actionId: Int
    )
}

interface HomeFeature : Feature<HomeFeature.Dependencies> {
    interface Dependencies {
        val context: Context
    }
}

interface VideoFeature : Feature<VideoFeature.Dependencies> {
    interface Dependencies {
        val okHttpClient: OkHttpClient
        val context: Context
        val handler: Handler
        val backgroundDispatcher: CoroutineDispatcher
    }
}
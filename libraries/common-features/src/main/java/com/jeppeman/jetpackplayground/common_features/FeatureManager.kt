package com.jeppeman.jetpackplayground.common_features

import android.content.Context
import android.content.IntentSender
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.play.core.splitinstall.SplitInstallManager
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallSessionState
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import java.util.*
import kotlin.math.roundToInt
import kotlin.reflect.KClass

fun createFeatureManager(context: Context): FeatureManager = FeatureManagerImpl(context)

inline fun <reified T : Feature<D>, D> FeatureManager.getFeature(
        dependencies: D
): T? = getFeature(T::class, dependencies)

inline fun <reified T : Feature<*>> FeatureManager.installFeature(
        noinline onStateUpdate: (FeatureManager.InstallState) -> Unit
) = installFeature(T::class, onStateUpdate)

inline fun <reified T : Feature<*>> FeatureManager.isFeatureInstalled(): Boolean = isFeatureInstalled(T::class)

interface FeatureManager {
    fun <T : Feature<D>, D> getFeature(featureType: KClass<T>, dependencies: D): T?
    fun <T : Feature<*>> installFeature(featureType: KClass<T>, onStateUpdate: (InstallState) -> Unit)
    fun <T : Feature<*>> isFeatureInstalled(featureType: KClass<T>): Boolean

    sealed class InstallState(val featureInfo: Feature.Info) {
        class Downloading(val progress: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Installing(val progress: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class RequiresUserConfirmation(val sender: IntentSender?, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Failed(val code: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Installed(featureInfo: Feature.Info) : InstallState(featureInfo)
    }
}

internal class FeatureManagerImpl(
        private val context: Context
) : FeatureManager {

    private val splitInstallManager: SplitInstallManager = SplitInstallManagerFactory.create(context)
    private val cache = mutableMapOf<KClass<out Feature<*>>, Feature<*>>()

    private fun SplitInstallSessionState.progress(): Int {
        return ((bytesDownloaded() / totalBytesToDownload().toFloat()) * 100).roundToInt()
    }

    private fun <T : Feature<*>> handleDownloadingState(
            state: SplitInstallSessionState,
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        val progress = state.progress()
        onStateUpdate(FeatureManager.InstallState.Downloading(progress, featureType.info(context)))
    }

    private fun <T : Feature<*>> handleInstallingState(
            state: SplitInstallSessionState,
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        val progress = state.progress()
        onStateUpdate(FeatureManager.InstallState.Installing(progress, featureType.info(context)))
    }

    private fun <T : Feature<*>> handleUserConfirmationRequired(
            state: SplitInstallSessionState,
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        onStateUpdate(FeatureManager.InstallState.RequiresUserConfirmation(
                sender = state.resolutionIntent()?.intentSender,
                featureInfo = featureType.info(context)
        ))
    }

    private fun <T : Feature<*>> handleInstalled(
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        onStateUpdate(FeatureManager.InstallState.Installed(featureType.info(context)))
    }

    private fun <T : Feature<*>> handleFailed(
            state: SplitInstallSessionState,
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        onStateUpdate(FeatureManager.InstallState.Failed(
                code = state.errorCode(),
                featureInfo = featureType.info(context)
        ))
    }

    override fun <T : Feature<*>> installFeature(
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        val request = SplitInstallRequest.newBuilder()
                .addModule(featureType.info(context).id)
                .build()

        val installStateUpdateListener = object : SplitInstallStateUpdatedListener {
            override fun onStateUpdate(state: SplitInstallSessionState) {
                state.moduleNames().forEach { _ ->
                    when (state.status()) {
                        SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                            handleUserConfirmationRequired(state, featureType, onStateUpdate)
                        }
                        SplitInstallSessionStatus.DOWNLOADING -> {
                            handleDownloadingState(state, featureType, onStateUpdate)
                        }
                        SplitInstallSessionStatus.INSTALLING -> {
                            handleInstallingState(state, featureType, onStateUpdate)
                        }
                        SplitInstallSessionStatus.INSTALLED -> {
                            splitInstallManager.unregisterListener(this)
                            handleInstalled(featureType, onStateUpdate)
                        }
                        SplitInstallSessionStatus.FAILED -> {
                            splitInstallManager.unregisterListener(this)
                            handleFailed(state, featureType, onStateUpdate)
                        }
                    }
                }
            }
        }

        splitInstallManager.registerListener(installStateUpdateListener)
        splitInstallManager.startInstall(request)
    }

    override fun <T : Feature<*>> isFeatureInstalled(featureType: KClass<T>): Boolean {
        return splitInstallManager.installedModules.contains(featureType.info(context).id)
    }

    override fun <T : Feature<D>, D> getFeature(featureType: KClass<T>, dependencies: D): T? {
        return try {
            val cachedFeature = cache[featureType]
            if (cachedFeature != null) {
                return featureType.java.cast(cachedFeature)
            }

            val serviceLoader = ServiceLoader.load(featureType.java, featureType.java.classLoader)
            if (serviceLoader.iterator().hasNext()) {
                val feature = serviceLoader.iterator().next()
                feature.inject(dependencies)
                feature.also { cache[featureType] = it }
            } else {
                null
            }
        } catch (exception: Exception) {
            null
        }
    }
}
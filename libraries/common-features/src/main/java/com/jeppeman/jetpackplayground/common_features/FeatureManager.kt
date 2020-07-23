package com.jeppeman.jetpackplayground.common_features

import android.content.Context
import android.content.IntentSender
import android.util.Log
import com.jeppeman.globallydynamic.globalsplitinstall.*
import java.util.*
import kotlin.math.roundToInt
import kotlin.reflect.KClass

fun createFeatureManager(context: Context): FeatureManager = FeatureManagerImpl(context)

inline fun <reified T : Feature<D>, D> FeatureManager.getFeature(
        dependencies: D
): T? {
    return if (isFeatureInstalled<T>()) {
        val serviceIterator = ServiceLoader.load(
                T::class.java,
                T::class.java.classLoader
        ).iterator()

        if (serviceIterator.hasNext()) {
            val feature = serviceIterator.next()
            feature.apply { inject(dependencies) }
        } else {
            null
        }
    } else {
        null
    }
}

inline fun <reified T : Feature<*>> FeatureManager.installFeature(
        noinline onStateUpdate: (FeatureManager.InstallState) -> Unit
) = installFeature(T::class, onStateUpdate)

inline fun <reified T : Feature<*>> FeatureManager.isFeatureInstalled(): Boolean = isFeatureInstalled(T::class)

interface FeatureManager {
    fun installMissingSplits(onStateUpdate: (GlobalSplitInstallSessionState) -> Unit)
    fun <T : Feature<*>> installFeature(featureType: KClass<T>, onStateUpdate: (InstallState) -> Unit)
    fun <T : Feature<*>> isFeatureInstalled(featureType: KClass<T>): Boolean
    fun registerInstallListener(listener: (Feature.Info) -> Unit)
    fun unregisterInstallListener(listener: (Feature.Info) -> Unit)
    fun registerMissingSplitsInstalledListener(listener: () -> Unit)
    fun unregisterMissingSplitsInstalledListener(listener: () -> Unit)

    sealed class InstallState(val featureInfo: Feature.Info) {
        class Downloading(val progress: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Installing(val progress: Int, featureInfo: Feature.Info) : InstallState(featureInfo)
        class RequiresUserConfirmation(val sender: IntentSender?, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Failed(val code: Int, val message: String, featureInfo: Feature.Info) : InstallState(featureInfo)
        class Installed(featureInfo: Feature.Info) : InstallState(featureInfo)
    }
}

internal class FeatureManagerImpl(
        private val context: Context
) : FeatureManager {

    private val splitInstallManager: GlobalSplitInstallManager = GlobalSplitInstallManagerFactory.create(context)
    private val installListeners = mutableListOf<(Feature.Info) -> Unit>()
    private val missingSplitsInstallListeners = mutableListOf<() -> Unit>()

    private fun GlobalSplitInstallSessionState.progress(): Int {
        return ((bytesDownloaded() / totalBytesToDownload().toFloat()) * 100).roundToInt()
    }

    private fun <T : Feature<*>> handleDownloadingState(
            state: GlobalSplitInstallSessionState,
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        val progress = state.progress()
        onStateUpdate(FeatureManager.InstallState.Downloading(progress, featureType.info(context)))
    }

    private fun <T : Feature<*>> handleInstallingState(
            state: GlobalSplitInstallSessionState,
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        val progress = state.progress()
        onStateUpdate(FeatureManager.InstallState.Installing(progress, featureType.info(context)))
    }

    private fun <T : Feature<*>> handleUserConfirmationRequired(
            state: GlobalSplitInstallSessionState,
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
            @GlobalSplitInstallErrorCode errorCode: Int,
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        onStateUpdate(FeatureManager.InstallState.Failed(
                code = errorCode,
                message = GlobalSplitInstallErrorCodeHelper.getErrorDescription(errorCode),
                featureInfo = featureType.info(context)
        ))
    }

    override fun installMissingSplits(onStateUpdate: (GlobalSplitInstallSessionState) -> Unit) {
        splitInstallManager.registerListener(onStateUpdate)
        splitInstallManager.installMissingSplits()
                .addOnCompleteListener {
                    missingSplitsInstallListeners.forEach { it() }
                    splitInstallManager.unregisterListener(onStateUpdate)
                }
    }

    override fun <T : Feature<*>> installFeature(
            featureType: KClass<T>,
            onStateUpdate: (FeatureManager.InstallState) -> Unit
    ) {
        val request = GlobalSplitInstallRequest.newBuilder()
                .addModule(featureType.info(context).id)
                .build()

        val installStateUpdateListener = object : GlobalSplitInstallUpdatedListener {
            override fun onStateUpdate(state: GlobalSplitInstallSessionState) {
                state.moduleNames().forEach { _ ->
                    when (state.status()) {
                        GlobalSplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
                            Log.d("FeatureManager", "REQUIRES_USER_CONFIRMATION")
                            handleUserConfirmationRequired(state, featureType, onStateUpdate)
                        }
                        GlobalSplitInstallSessionStatus.DOWNLOADING -> {
                            Log.d("FeatureManager", "DOWNLOADING")
                            handleDownloadingState(state, featureType, onStateUpdate)
                        }
                        GlobalSplitInstallSessionStatus.INSTALLING -> {
                            Log.d("FeatureManager", "INSTALLING")
                            handleInstallingState(state, featureType, onStateUpdate)
                        }
                        GlobalSplitInstallSessionStatus.INSTALLED -> {
                            Log.d("FeatureManager", "INSTALLED")
                            splitInstallManager.unregisterListener(this)
                            installListeners.forEach { listener ->
                                listener(featureType.info(context))
                            }
                            handleInstalled(featureType, onStateUpdate)
                        }
                        GlobalSplitInstallSessionStatus.FAILED -> {
                            Log.d("FeatureManager", "FAILED")
                            splitInstallManager.unregisterListener(this)
                            handleFailed(state.errorCode(), featureType, onStateUpdate)
                        }
                    }
                }
            }
        }

        splitInstallManager.registerListener(installStateUpdateListener)
        splitInstallManager.startInstall(request)
                .addOnFailureListener { exception ->
                    handleFailed((exception as? GlobalSplitInstallException)?.errorCode ?: 0,
                    featureType, onStateUpdate)
                }
    }

    override fun registerInstallListener(listener: (Feature.Info) -> Unit) {
        installListeners.add(listener)
    }

    override fun unregisterInstallListener(listener: (Feature.Info) -> Unit) {
        installListeners.remove(listener)
    }

    override fun registerMissingSplitsInstalledListener(listener: () -> Unit) {
        missingSplitsInstallListeners.add(listener)
    }

    override fun unregisterMissingSplitsInstalledListener(listener: () -> Unit) {
        missingSplitsInstallListeners.remove(listener)
    }

    override fun <T : Feature<*>> isFeatureInstalled(featureType: KClass<T>): Boolean {
        return splitInstallManager.installedModules.contains(featureType.info(context).id)
    }
}

fun GlobalSplitInstallSessionState.progress(): Int {
    return ((bytesDownloaded() / totalBytesToDownload().toFloat()) * 100).roundToInt()
}
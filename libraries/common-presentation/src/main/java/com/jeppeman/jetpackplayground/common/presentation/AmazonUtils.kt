package com.jeppeman.jetpackplayground.common.presentation

import android.content.Context
import android.util.Log

// Hacky workaround for bug on Amazon Fire TV devices
fun Context.stripFireOsAssets() {
    try {
        val assetManagerClass = assets::class.java
        assetManagerClass.declaredMethods.forEach { method ->
            Log.d("${method.name}", method.parameterTypes.map { it.canonicalName }.toString())
        }
        val getApkAssetsMethod = assetManagerClass.getDeclaredMethod("getApkAssets")
        getApkAssetsMethod.isAccessible = true
        val apkAssets = getApkAssetsMethod.invoke(assets) as Array<Any>
        apkAssets.forEachIndexed { index, apkAsset ->
            val getAssetPathMethod = apkAsset::class.java.getDeclaredMethod("getAssetPath")
            getAssetPathMethod.isAccessible = true
            val fireOs = getAssetPathMethod.invoke(apkAsset).toString().contains("fireos")
            if (fireOs) {
                apkAssets[index] = apkAssets[index + 1]
            }
        }
        val obj = assetManagerClass.getDeclaredField("mObject")
        obj.isAccessible = true
        val handle = obj.get(assets)
        val accessMethod = assetManagerClass.declaredMethods.first { it.name == "access$300" }
        accessMethod.isAccessible = true
        accessMethod.invoke(null, handle, apkAssets, true)
    } catch (exception: Exception) {
        Log.e("Yolo", "Exception", exception)
    }
}


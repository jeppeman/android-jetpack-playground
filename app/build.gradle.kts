import com.jeppeman.globallydynamic.gradle.extensions.globallyDynamicServers

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.jeppeman.globallydynamic")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val applicationIdBase: String by rootProject.extra

android {
    defaultConfig {
        applicationId = applicationIdBase
    }

    if (project.hasProperty("KEYSTORE_PATH")) {
        signingConfigs {
            create("release") {
                storeFile = file(project.property("KEYSTORE_PATH")!!.toString())
                storePassword = project.property("KEYSTORE_PASSWORD")?.toString()
                keyAlias = project.property("KEY_ALIAS")?.toString()
                keyPassword = project.property("KEY_PASSWORD")?.toString()
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            if (project.hasProperty("KEYSTORE_PATH")) {
                 signingConfig = signingConfigs.getByName("release")
            }
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    globallyDynamicServers {
        create("studioIntegrated") {
            throttleDownloadBy = 2000
            applyToBuildVariants("huaweiDebug", "galaxyDebug", "amazonDebug", "gplayDebug")
        }
        create("prod") {
            serverUrl = "https://globallydynamic.io/api"
            username = project.property("GLOBALLY_DYNAMIC_USERNAME")?.toString() ?: ""
            password = project.property("GLOBALLY_DYNAMIC_PASSWORD")?.toString() ?: ""
            applyToBuildVariants("galaxyRelease", "amazonRelease")
        }
    }

    dynamicFeatures = mutableSetOf(":features:video", ":features:home")
}

val deps: Map<String, Any> by rootProject.extra
val kotlin: Map<String, Any> by deps
val dagger: Map<String, Any> by deps
val coroutines: Map<String, Any> by deps
val constraintlayout: Map<String, Any> by deps
val moshi: Map<String, Any> by deps
val okhttp: Map<String, Any> by deps
val retrofit: Map<String, Any> by deps
val material: Map<String, Any> by deps
val navigation: Map<String, Any> by deps
val lifecycle: Map<String, Any> by deps
val core: Map<String, Any> by deps
val timber: Map<String, Any> by deps
val test: Map<String, Any> by deps
val autoservice: Map<String, Any> by deps
val firebase: Map<String, Any> by deps

dependencies {
    api(kotlin["stdlib"] ?: error(""))
    api(dagger["core"] ?: error(""))
    api(dagger["android"] ?: error(""))
    api(dagger["support"] ?: error(""))
    api(coroutines["core"] ?: error(""))
    api(coroutines["android"] ?: error(""))
    api(constraintlayout["constraintlayout"] ?: error(""))
    api(moshi["moshi"] ?: error(""))
    api(okhttp["okhttp"] ?: error(""))
    api(retrofit["retrofit"] ?: error(""))
    api(retrofit["moshi_converter"] ?: error(""))
    api(material["material"] ?: error(""))
    api(navigation["fragment"] ?: error(""))
    api(navigation["runtime"] ?: error(""))
    api(navigation["ui"] ?: error(""))
    api(lifecycle["livedata"] ?: error(""))
    api(lifecycle["viewmodel"] ?: error(""))
    api(lifecycle["viewmodelext"] ?: error(""))
    api(core["ktx"] ?: error(""))
    api(timber["timber"] ?: error(""))
    api(test["core"] ?: error(""))
    api(test["fragment"] ?: error(""))
    api(autoservice["annotations"] ?: error(""))
    api(firebase["crashlytics"] ?: error(""))
    api(firebase["analytics"] ?: error(""))
    api(project(":libraries:common-presentation"))
    api(project(":libraries:common-domain"))
    api(project(":libraries:common-data"))
    api(project(":libraries:common-features"))

    kapt(dagger["compiler"] ?: error(""))
    kapt(dagger["android_compiler"] ?: error(""))
    kapt(autoservice["processor"] ?: error(""))
}

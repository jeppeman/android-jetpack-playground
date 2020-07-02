import com.jeppeman.locallydynamic.gradle.extensions.locallyDynamic

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    id("kotlin-kapt")
    id("com.jeppeman.locallydynamic")
}

val applicationIdBase: String by rootProject.extra

android {
    defaultConfig {
        applicationId = applicationIdBase
    }

//    signingConfigs {
//        release {
//            storeFile file(getProperty("KEYSTORE_PATH"))
//            storePassword getProperty("KEYSTORE_PASSWORD")
//            keyAlias getProperty("KEY_ALIAS")
//            keyPassword getProperty("KEY_PASSWORD")
//        }
//    }

    buildTypes {
        getByName("debug") {
            locallyDynamic {
                enabled = true
                throttleDownloadBy = 2000
            }
        }
        getByName("release") {
            isMinifyEnabled = true
//            signingConfig signingConfigs.release
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    dynamicFeatures = mutableSetOf(":features:video", ":features:home")
}

val deps: Map<String, Any> by rootProject.extra
val kotlin: Map<String, Any> by deps
val dagger: Map<String, Any> by deps
val play: Map<String, Any> by deps
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

dependencies {
    api(kotlin["stdlib"] ?: error(""))
    api(dagger["core"] ?: error(""))
    api(dagger["android"] ?: error(""))
    api(dagger["support"] ?: error(""))
    api(play["core"] ?: error(""))
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
    api(project(":libraries:common-presentation"))
    api(project(":libraries:common-domain"))
    api(project(":libraries:common-data"))
    api(project(":libraries:common-features"))

    kapt(dagger[ "compiler" ] ?: error(""))
    kapt(dagger[ "android_compiler" ] ?: error(""))
    kapt(autoservice[ "processor" ] ?: error(""))
}

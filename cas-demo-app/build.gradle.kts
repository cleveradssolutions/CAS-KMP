import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.native.cocoapods)
    // Official CAS.AI Android Gradle Plugin: resolves the Android mediation adapters and adds
    // the manifest entries they need. Must be applied after the Android plugin.
    alias(libs.plugins.cas.android)
}

// Android mediation adapters. The iOS ones are declared in `iosApp/Podfile`; the two lists are
// chosen per platform on purpose, because network availability differs between them.
//
// This sample keeps the list short so it builds quickly. A production application normally
// starts from `includeOptimalAds = true`, the set of networks CAS.AI recommends and keeps up
// to date, and adds individual networks on top of it.
cas {
    adapters {
        googleAds = true
        unityAds = true
    }
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        summary = "CAS KMP Demo app"
        homepage = "https://cas.ai"
        version = "1.0"
        ios.deploymentTarget = libs.versions.ios.deploymentTarget.get()

        framework {
            baseName = "CASDemoApp"
            isStatic = true
        }

        // The CAS.AI SDK and its iOS mediation adapters are declared in `iosApp/Podfile`.
        // The Kotlin bindings for the SDK come from the `cas-kmp` dependency below, so this
        // module declares no `pod` of its own.
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":cas-kmp"))
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.kotlinx.coroutines)
        }

        androidMain.dependencies {
            implementation(libs.activity.compose)
            implementation(libs.lifecycle.runtime.ktx)
            // `com.cleveradssolutions:cas-sdk` and every adapter selected in the `cas { }`
            // block above are added by the CAS.AI Android Gradle Plugin.
        }
    }
}

android {
    namespace = "com.cleveradssolutions.kmp.demo"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.cleveradssolutions.kmp.demo"
        minSdk = libs.versions.android.demoMinSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets.getByName("main") {
        res.srcDirs("src/main/res")
    }
}

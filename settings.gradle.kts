rootProject.name = "cas-kmp"

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        google()
        mavenCentral()

        // Several ad networks do not publish their Android SDK to Maven Central, so the CAS.AI
        // mediation adapter of such a network cannot be resolved without its own repository.
        // Each one is restricted to the groups it serves, so the rest of the build is unaffected.
        // Keep in sync with https://github.com/cleveradssolutions/CAS-Android/blob/master/settings.gradle
        maven {
            name = "MintegralAdsRepo"
            setUrl("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
            content { includeGroup("com.mbridge.msdk.oversea") }
        }
        maven {
            name = "PangleAdsRepo"
            setUrl("https://artifact.bytedance.com/repository/pangle")
            content { includeGroup("com.pangle.global") }
        }
        maven {
            name = "ChartboostAdsRepo"
            setUrl("https://cboost.jfrog.io/artifactory/chartboost-ads/")
            content {
                includeGroup("com.chartboost")
                includeGroup("com.iab.omid.library")
            }
        }
        maven {
            name = "YsoNetworkRepo"
            setUrl("https://ysonetwork.s3.eu-west-3.amazonaws.com/sdk/android")
            content { includeGroup("com.ysocorp") }
        }
        maven {
            name = "OguryAdsRepo"
            setUrl("https://maven.ogury.co")
            content {
                includeGroup("co.ogury")
                includeGroup("co.ogury.module")
            }
        }
        maven {
            name = "SmaatoAdsRepo"
            setUrl("https://s3.amazonaws.com/smaato-sdk-releases/")
            content {
                includeGroup("com.smaato.android.sdk")
                includeGroup("com.verve")
            }
        }
        maven {
            name = "VerveAdsRepo"
            setUrl("https://verve.jfrog.io/artifactory/verve-gradle-release")
            content {
                includeGroup("net.pubnative")
                includeGroup("com.verve")
                includeGroup("com.smaato.android.sdk")
            }
        }
        maven {
            name = "SuperAwesomeRepo"
            setUrl("https://aa-sdk.s3-eu-west-1.amazonaws.com/android_repo/")
            content { includeGroup("tv.superawesome.sdk.publisher") }
        }
        maven {
            name = "PubMaticAdsRepo"
            setUrl("https://repo.pubmatic.com/artifactory/public-repos")
            content { includeGroup("com.pubmatic.sdk") }
        }
    }
}

include(":cas-kmp")
include(":cas-demo-app")

import com.vanniktech.maven.publish.DeploymentValidation
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.multiplatform.library)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.native.cocoapods)
    alias(libs.plugins.maven.publish)
}

group = "com.cleveradssolutions"
version = libs.versions.casKmp.get()

kotlin {
    explicitApi()

    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            compileOnly(libs.compose.foundation)
            api(libs.compose.foundation)
        }

        androidMain.dependencies {
            compileOnly(libs.cas.sdk)
        }
    }

    compilerOptions {
        freeCompilerArgs.add("-Xexpect-actual-classes")
    }

    android {
        namespace = "com.cleveradssolutions.plugin.kmp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()

        withJava()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    cocoapods {
        summary = "CAS.AI Mediation for Kotlin Multiplatform"
        homepage = "https://cas.ai"
        authors = "Clever Ads Solutions LTD"
        license = "Commercial"
        version = libs.versions.casKmp.get()
        ios.deploymentTarget = libs.versions.ios.deploymentTarget.get()

        framework {
            baseName = "CAS_KMP"
            isStatic = true
        }

        specRepos {
            url("https://github.com/cleveradssolutions/CAS-Specs.git")
        }

        pod("CleverAdsSolutions-Base") {
            version = libs.versions.cas.get()
            moduleName = "CleverAdsSolutions"
            extraOpts += listOf("-compiler-option", "-fmodules")
        }
    }
}

// Publishing credentials reach Gradle as ORG_GRADLE_PROJECT_<name> environment variables, which
// updater.py takes from local.properties. The publishing plugin resolves the Maven Central ones
// before the project is configured, so a properties file read here could not reach it anyway.
fun property(name: String): String? = findProperty(name)?.toString()?.takeIf { it.isNotBlank() }

val hasKeyRingSigning = property("signing.keyId") != null && property("signing.password") != null
val hasInMemorySigning = property("signingInMemoryKey") != null
val hasSigningKey = hasKeyRingSigning || hasInMemorySigning

mavenPublishing {
    // Maven Central only asks for a javadoc jar next to a `.jar`, which here is the root artifact
    // alone: the others ship an `.aar` or a `.klib`. Kotlin has no javadoc anyway, so the plugin
    // would attach an empty jar to each of them.
    configure(
        com.vanniktech.maven.publish.KotlinMultiplatform(
            javadocJar = com.vanniktech.maven.publish.JavadocJar.None(),
            sourcesJar = true,
        ),
    )

    coordinates(group.toString(), "cas-kmp", version.toString())

    // Upload and wait until Central has validated the deployment, then stop. Releasing stays a
    // manual step in the portal. Without the explicit validation the task returns immediately and
    // a rejected deployment looks exactly like a successful one.
    publishToMavenCentral(false, DeploymentValidation.VALIDATED)

    // Maven Central requires signed artifacts. Publishing to the local repository does not, so the
    // signing is only configured when a key is available.
    if (hasSigningKey) {
        signAllPublications()
    }

    pom {
        name.set("CAS.AI Mobile Ads for Kotlin Multiplatform")
        description.set(
            "Kotlin Multiplatform plugin for CAS.AI mediation, with a shared ad API for Android " +
                "and iOS and Compose Multiplatform composables for banner and native ads.",
        )
        inceptionYear.set("2025")
        url.set("https://github.com/cleveradssolutions/CAS-KMP")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("cleveradssolutions")
                name.set("Clever Ads Solutions LTD")
                email.set("support@cas.ai")
                url.set("https://cas.ai")
            }
        }

        scm {
            url.set("https://github.com/cleveradssolutions/CAS-KMP")
            connection.set("scm:git:git://github.com/cleveradssolutions/CAS-KMP.git")
            developerConnection.set("scm:git:ssh://git@github.com/cleveradssolutions/CAS-KMP.git")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "localBuild"
            url = uri(rootProject.layout.buildDirectory.dir("repo").get().asFile)
        }
    }
}

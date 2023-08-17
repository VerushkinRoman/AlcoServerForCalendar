@file:Suppress("UNUSED_VARIABLE")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.android.application)
    alias(libs.plugins.serialization)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop")

    sourceSets {
        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }

        val commonMain by getting {
            dependencies {
                api(compose.foundation)
                api(compose.animation)
                implementation(compose.runtime)
//                implementation(compose.material)
                implementation(compose.material3)
//                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
//                implementation(compose.components.resources)
                implementation(libs.kotlinx.coroutines.core)
                api(libs.precompose)
                api(libs.precompose.viewmodel)
//                implementation(libs.firebase.firestore)
                implementation(libs.firebase.admin)
//                implementation(libs.serialization)
                implementation(libs.multiplatform.settings)
                implementation(libs.kodein.di)
                implementation(libs.mpfilepicker)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.appcompat)
//                implementation(libs.compose.uitooling)
                implementation(libs.kotlinx.coroutines.android)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.common)
                implementation(compose.desktop.currentOs)
            }
        }

    }
}

android {
    namespace = "ru.alcoserver.verushkinrg"
    compileSdk = 34

    defaultConfig {
        minSdk = 30
        targetSdk = 34

        applicationId = "ru.alcoserver.verushkinrg.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        res.srcDirs("src/androidMain/resources")
        resources.srcDirs("src/commonMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    @Suppress("DEPRECATION")
    packagingOptions {
        resources.excludes.add("META-INF/**")
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ru.alcoserver.verushkinrg.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}

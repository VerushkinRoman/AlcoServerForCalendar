import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation("org.igniterealtime.smack:smack:3.2.1")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            windows {
                iconFile.set(project.file("logo.ico"))
            }
            targetFormats(TargetFormat.Exe)
            packageName = "AlcoServer"
            packageVersion = "1.0.0"
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }
}
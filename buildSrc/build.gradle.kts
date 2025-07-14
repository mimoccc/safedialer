/*
 *  Copyright (c) Milan Jurkulák 2024.
 *  Contact:
 *  e: mimoccc@gmail.com
 *  e: mj@mjdev.org
 *  w: https://mjdev.org
 */

plugins {
    `kotlin-dsl`
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
    maven("https://androidx.dev/storage/compose-compiler/repository")
    maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
    maven("https://gitlab.com/api/v4/projects/38224197/packages/maven")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://jogamp.org/deployment/maven")
    maven("https://jitpack.io")
    maven("https://maven.mozilla.org/maven2/")
    maven("https://maven.pkg.github.com/tuProlog/2p-kt")
    maven("https://dl.bintray.com/animeshz/maven")
    maven("https://plugins.gradle.org/m2/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://dl.bintray.com/kotlin/ktor")
    maven("https://central.sonatype.com/repository/maven-snapshots")
    google()
}

dependencies {
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
//    implementation(libs.kotlin.reflect)
//    implementation(libs.gradle.api)
//    implementation(libs.gradle)
//    implementation(libs.gradle.kotlin.plugin)
}

gradlePlugin {
    plugins {
        register("ProjectPlugin") {
            id = "ProjectPlugin"
            displayName = "ProjectPlugin"
            description = "Common library plugin to handle all stuffs needed."
            implementationClass = "ProjectPlugin"
        }
    }
}

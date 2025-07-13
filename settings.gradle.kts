@file:Suppress("UnstableApiUsage")

rootProject.name = "SafeDialer"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/releases")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://androidx.dev/storage/compose-compiler/repository")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        maven("https://gitlab.com/api/v4/projects/38224197/packages/maven")
        maven("https://jogamp.org/deployment/maven")
        maven("https://maven.mozilla.org/maven2/")
        maven("https://maven.pkg.github.com/tuProlog/2p-kt")
        maven("https://dl.bintray.com/animeshz/maven")
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://central.sonatype.com/repository/maven-snapshots")
        maven("https://storage.googleapis.com/r8-releases/raw")
        maven("https://mvn.dailymotion.com/repository/releases/")
        maven("https://mobile.maven.couchbase.com/maven2/dev/")
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/wasm/experimental")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
        maven("https://oss.sonatype.org/content/repositories/releases")
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://androidx.dev/storage/compose-compiler/repository")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/dev")
        maven("https://gitlab.com/api/v4/projects/38224197/packages/maven")
        maven("https://jogamp.org/deployment/maven")
        maven("https://maven.mozilla.org/maven2/")
        maven("https://maven.pkg.github.com/tuProlog/2p-kt")
        maven("https://dl.bintray.com/animeshz/maven")
        maven("https://plugins.gradle.org/m2/")
        maven("https://dl.bintray.com/kotlin/ktor")
        maven("https://central.sonatype.com/repository/maven-snapshots")
        maven("https://storage.googleapis.com/r8-releases/raw")
        maven("https://mvn.dailymotion.com/repository/releases/")
        maven("https://mobile.maven.couchbase.com/maven2/dev/")
        google()
    }
}

include(":composeApp")

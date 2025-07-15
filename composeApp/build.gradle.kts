import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    ProjectPlugin
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JVM_17)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.okhttp)
            implementation(libs.telecom.core)
            implementation(libs.libphonenumber)
            implementation(libs.androidx.annotation)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.lottie.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.base)
            implementation(libs.permissions)
            implementation(libs.ktor.server.core)
            implementation(libs.ktor.server.netty)
            implementation(libs.ktor.server.default.headers)
            implementation(libs.ktor.server.call.logging)
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.serialization.gson)
            implementation(libs.ktor.server.status.pages)
            implementation(libs.ktor.server.cors)
            implementation(libs.ktor.server.auth)
            implementation(libs.ktor.server.compression)
            implementation(libs.ktor.server.websockets)
            implementation(libs.haze.jetpack.compose)
            implementation(libs.gson)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.qrose)
            implementation(libs.couchbase.lite)
            implementation(libs.logback.classic)
            implementation(libs.kodein.di)
            implementation(libs.kodein.di.framework.compose)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = libs.versions.android.appnamespace.stringValue
    compileSdk = libs.versions.android.compileSdk.intValue
    defaultConfig {
        applicationId = libs.versions.android.appnamespace.stringValue
        minSdk = libs.versions.android.minSdk.intValue
        //noinspection OldTargetApi
        targetSdk = libs.versions.android.targetSdk.intValue
        versionCode = libs.versions.android.versionCode.intValue
        versionName = libs.versions.android.versionName.stringValue
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/license.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/notice.txt"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/INDEX.LIST"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = VERSION_17
        targetCompatibility = VERSION_17
    }
    lint {
        htmlReport = true
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds = false
        abortOnError = false
        quiet = true
        ignoreWarnings = true
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

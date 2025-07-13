import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
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
            implementation("com.nabinbhandari.android:permissions:4.0.0")
            implementation("io.ktor:ktor-server-core:3.2.1")
            implementation("io.ktor:ktor-server-netty:3.2.1")
            implementation("io.ktor:ktor-server-default-headers:3.2.1")
            implementation("io.ktor:ktor-server-call-logging:3.2.1")
            implementation("io.ktor:ktor-server-content-negotiation:3.2.1")
            implementation("io.ktor:ktor-serialization-gson:3.2.1")
            implementation("io.ktor:ktor-server-status-pages:3.2.1")
            implementation("io.ktor:ktor-server-cors:3.2.1")
            implementation("io.ktor:ktor-server-auth:3.2.1")
            implementation("io.ktor:ktor-server-compression:3.2.1")
            implementation("io.ktor:ktor-server-websockets:3.2.1")
            implementation("dev.chrisbanes.haze:haze-jetpack-compose:0.7.0")
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
            implementation(libs.qrose)
            implementation(libs.coil.compose)
            implementation(libs.coil.base)
            implementation("com.google.code.gson:gson:2.10.1")
            implementation("dev.kotbase:couchbase-lite:3.1.9-1.1.1")
            implementation("ch.qos.logback:logback-classic:1.3.11")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "org.mjdev.safedialer"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "org.mjdev.safedialer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

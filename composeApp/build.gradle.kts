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
            implementation("com.google.code.gson:gson:2.10.1")
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
            implementation("dev.kotbase:couchbase-lite:3.1.9-1.1.1")
            implementation("ch.qos.logback:logback-classic:1.3.11")
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

// todo remove to plugin

// tasks.register("updateVersionInReadme") {
//    group = "mjdev"
//    onlyIf { System.getenv("CI") == "true" }
//    doLast {
//        val version = android.defaultConfig.versionName
//        val readmeFile = rootProject.rootDir.resolve("site").resolve("index.md")
//        val content = readmeFile.readText()
//        val newContent = content.replace("%%VERSION%%", "v$version")
//        readmeFile.writeText(newContent)
//    }
// }

// tasks.register<Exec>("jekyllBuild") {
//    group = "mjdev"
// //    onlyIf { System.getenv("CI") == "true" }
//    commandLine("jekyll", "build", "-s", "./site", "-d", "./_site")
// }

// tasks.register("deleteTemporarFiles") {
//    group = "mjdev"
//    doLast {
//        delete(rootDir.resolve(".jekyll-cache"))
//        delete(rootDir.resolve("_site"))
//        delete(rootDir.resolve(".kotlin"))
//    }
// }

// tasks.named("build") {
//    dependsOn("updateVersionInReadme", "jekyllBuild")
// }

// tasks.named("clean") {
//    dependsOn("deleteTemporarFiles")
// }

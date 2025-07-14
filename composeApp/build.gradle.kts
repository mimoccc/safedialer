import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    id("dev.adamko.dokkatoo-jekyll") version "2.3.1"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
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
    namespace = "org.mjdev.safedialer"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        applicationId = "org.mjdev.safedialer"
        minSdk = libs.versions.android.minSdk.get().toInt()
        //noinspection OldTargetApi
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.android.versionCode.get().toInt()
        versionName = libs.versions.android.versionName.get().toString()
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        abortOnError = false
        htmlReport = true
        baseline = file("lint-baseline.xml")
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

//dokkatoo {
//    moduleName.set("Basic Project")
//
//    dokkatooSourceSets.configureEach {
//        documentedVisibilities(
//            VisibilityModifier.PUBLIC,
//            VisibilityModifier.PROTECTED,
//        )
//        suppressedFiles.from(file("src/main/kotlin/it/suppressedByPath"))
//        perPackageOption {
//            matchingRegex.set("it.suppressedByPackage.*")
//            suppress.set(true)
//        }
//        perPackageOption {
//            matchingRegex.set("it.overriddenVisibility.*")
//            documentedVisibilities(
//                DokkaConfiguration.Visibility.PRIVATE
//            )
//        }
//    }
//
//    pluginsConfiguration.html {
//        customStyleSheets.from(
//            "./customResources/logo-styles.css",
//            "./customResources/custom-style-to-add.css",
//        )
//        customAssets.from(
//            "./customResources/custom-resource.svg",
//        )
//        footerMessage.set("(C) The Owner")
//    }
//
//    dokkatooPublications.configureEach {
//        suppressObviousFunctions.set(true)
//        suppressInheritedMembers.set(false)
//    }
//
//    // The default versions that Dokkatoo uses can be overridden:
//    versions {
//        jetbrainsDokka.set("1.9.20")
//    }
//}

tasks.register("updateVersionInReadme") {
    group = "mjdev"
    onlyIf { System.getenv("CI") == "true" }
    doLast {
        val version = android.defaultConfig.versionName
        val readmeFile = rootProject.file("index.md")
        val content = readmeFile.readText()
        val newContent = content.replace("%%VERSION%%", "v$version")
        readmeFile.writeText(newContent)
    }
}

tasks.register("deleteTemporarFiles") {
    group = "mjdev"
    doLast {
        delete(rootDir.resolve(".jekyll-cache"))
        delete(rootDir.resolve("_site"))
        delete(rootDir.resolve(".kotlin"))
    }
}

tasks.named("build") {
    dependsOn("updateVersionInReadme")
}

tasks.named("clean") {
    dependsOn("deleteTemporarFiles")
}

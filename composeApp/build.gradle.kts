@file:OptIn(
    ExperimentalComposeLibrary::class,
    ExperimentalKotlinGradlePluginApi::class
)

import ProjectPlugin.Companion.credentialsMap
import com.android.build.api.dsl.VariantDimension
import org.gradle.api.JavaVersion.VERSION_17
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSetTree
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING
import com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN
import org.gradle.kotlin.dsl.kotlin

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.paparazzi)
    alias(libs.plugins.buildkonfig)
    ProjectPlugin
}

fun VariantDimension.authResValue(name: String) = resValue(
    "string",
    "authority_$name",
    "${(libs.versions.android.appnamespace).stringValue}.$name"
)

fun VariantDimension.syncAccountTypeResValue() = resValue(
    "string",
    "account_type",
    "${(libs.versions.android.appnamespace).stringValue}.sync_account"
)

val mjdevServer by credentialsMap
val mjdevServerUser by credentialsMap
val mjdevServerPass by credentialsMap

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
        unitTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        instrumentedTestVariant.sourceSetTree.set(KotlinSourceSetTree.test)
        dependencies {
            testImplementation(libs.kotlin.test)
            testImplementation(libs.androidx.core.ktx)
            testImplementation(libs.junit4)
            testImplementation(libs.androidx.test.core)
            testImplementation(libs.robolectric)
            androidTestImplementation(libs.junit4)
            androidTestImplementation(libs.androidx.test.core)
            androidTestImplementation(libs.androidx.test.ext.junit)
            androidTestImplementation(libs.androidx.test.runner)
            androidTestImplementation(libs.androidx.test.rules)
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
            implementation(libs.okhttp.logging)
            implementation(libs.telecom.core)
            implementation(libs.libphonenumber)
            implementation(libs.androidx.annotation)
            implementation(libs.androidx.core.splashscreen)
            implementation(libs.lottie.compose)
            implementation(libs.coil.compose)
            implementation(libs.coil.base)
            implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")
            implementation("io.coil-kt.coil3:coil-svg:3.3.0")
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
            implementation(libs.jakarta.mail)
            implementation(libs.jakarta.activation)
            implementation(libs.ez.vcard)
            implementation(libs.accompanist.permissions)

            implementation("org.bouncycastle:bcprov-jdk18on:1.78")
            implementation("org.bouncycastle:bcpg-jdk18on:1.78")
            implementation("org.bouncycastle:bcmail-jdk18on:1.78")
            implementation("org.bouncycastle:bcpkix-jdk18on:1.78")

            implementation("androidx.palette:palette-ktx:1.0.0")

            implementation("androidx.glance:glance:1.1.1")
            implementation("androidx.glance:glance-preview:1.1.1")
            implementation("androidx.glance:glance-appwidget-preview:1.1.1")
            implementation("androidx.glance:glance-appwidget:1.1.1")
            implementation("androidx.glance:glance-material3:1.1.1")
            implementation("androidx.glance:glance-material:1.1.1")

            implementation("com.google.android.glance.tools:appwidget-host:0.2.2")
            implementation("com.google.android.glance.tools:appwidget-preview:0.1.2")
            implementation("com.google.android.glance.tools:appwidget-viewer:0.2.2")

            implementation("androidx.media3:media3-exoplayer:1.2.0")
            implementation("androidx.media3:media3-ui:1.2.0")

            implementation("org.jsoup:jsoup:1.22.1")
            implementation("net.sf.biweekly:biweekly:0.6.8")
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
//            implementation(libs.couchbase.lite)
            implementation(libs.logback.classic)
            implementation(libs.kodein.di)
            implementation(libs.kodein.di.framework.compose)
//            implementation(libs.kodein.db)
//            implementation(libs.kodein.db.serializer.kotlinx)
//            implementation("com.cactuscompute:cactus:1.2.0-beta")
//            implementation("javax.sip:jain-sip-ri:1.3.0-91")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = (libs.versions.android.appnamespace).stringValue
    compileSdk = (libs.versions.android.compileSdk).intValue
    defaultConfig {
        applicationId = (libs.versions.android.appnamespace).stringValue
        minSdk = (libs.versions.android.minSdk).intValue
        // noinspection OldTargetApi
        targetSdk = (libs.versions.android.targetSdk).intValue
        versionCode = (libs.versions.android.versionCode).intValue
        versionName = (libs.versions.android.versionName).stringValue
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //
        syncAccountTypeResValue()
        //
        authResValue("calendar")
        authResValue("call_log")
        authResValue("contacts")
        authResValue("emails")
        authResValue("gallery")
        authResValue("tasks")
        authResValue("ai")
        authResValue("messages")
        authResValue("invoices")
        authResValue("notes")
        authResValue("authenticator")
        authResValue("documents")
        //
        resValue(
            "string",
            "app_name",
            mjdevServer.ifEmpty { (libs.versions.android.appName).stringValue }
        )
        //
        resValue("string", "sync_label_calendar", "Calendar")
        resValue("string", "sync_label_call_log", "CallLog")
        resValue("string", "sync_label_contacts", "Contacts")
        resValue("string", "sync_label_emails", "Emails")
        resValue("string", "sync_label_gallery", "Gallery")
        resValue("string", "sync_label_tasks", "Tasks")
        resValue("string", "sync_label_ai", "Ai")
        resValue("string", "sync_label_messages", "Messages")
        resValue("string", "sync_label_invoices", "Invoices")
        resValue("string", "sync_label_notes", "Notes")
        resValue("string", "sync_label_authenticator", "Authenticator")
        resValue("string", "sync_label_documents", "Documents")
    }
    packaging {
        resources {
            excludes += "META-INF/mailcap"
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/license.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/notice.txt"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/NOTICE.md"
            excludes += "META-INF/io.netty.versions.properties"
            excludes += "META-INF/INDEX.LIST"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            signingConfig = signingConfigs.getByName("debug")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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
    sourceSets {
        getByName("main") {
            res.srcDirs("$projectDir/composeApp/src/androidMain/res")
        }
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}

tasks.withType<Test> {
    jvmArgs(
        "-XX:+EnableDynamicAgentLoading",
    )
    systemProperty("project.rootDir", rootDir.absolutePath)
}

buildkonfig {
    val isDebug = gradle.startParameter.taskNames.any { t ->
        t.lowercase().contains("debug")
    }
    packageName = (libs.versions.android.appnamespace).stringValue
    objectName = "BuildConfig"
    defaultConfigs {
        buildConfigField(STRING, "APP_ID", (libs.versions.android.appnamespace).stringValue)
        buildConfigField(
            STRING,
            "APP_NAME",
            mjdevServer.ifEmpty { (libs.versions.android.appName).stringValue }
        )
        buildConfigField(BOOLEAN, "IS_DEBUG", isDebug.toString())
        buildConfigField(STRING, "SERVER", mjdevServer)
        buildConfigField(STRING, "SERVER_UNAME", mjdevServerUser)
        buildConfigField(STRING, "SERVER_UPASS", mjdevServerPass)
        buildConfigField(STRING, "SERVER_PORT_IMAP", (libs.versions.port.imap).stringValue)
        buildConfigField(STRING, "SERVER_PORT_SMTP", (libs.versions.port.smtp).stringValue)
        buildConfigField(
            STRING,
            "ACCOUNT_TYPE",
            "${(libs.versions.android.appnamespace).stringValue}.sync_account"
        )
    }
}

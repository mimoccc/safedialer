import java.net.URL
import java.security.CodeSource

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

val codeSourceLocation: URL
    get() = runCatching {
        @Suppress("UnresolvedReference")
        libs.javaClass.superclass.protectionDomain.codeSource.location
    }.getOrThrow()

val versionCatalogLibs =
    files(codeSourceLocation)

fun DependencyHandlerScope.plugin(
    plugin: Provider<PluginDependency>
): Provider<String> = plugin.map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

repositories {
    mavenLocal()
    gradlePluginPortal()
    mavenCentral()
    google()
}

dependencies {
    implementation(versionCatalogLibs)
//    implementation(plugin(libs.plugins.androidApplication))
//    implementation(plugin(libs.plugins.androidLibrary))
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
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

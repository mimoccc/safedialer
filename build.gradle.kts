plugins {
//    base
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

//afterEvaluate {
//    tasks.named<Delete>("clean") {
//        delete(rootProject.buildDir)
//        delete("/.jekyll-cache")
//        delete("/_site")
//    }
//}
package org.mjdev.safedialer.helpers

import com.google.gson.Gson
import com.google.gson.GsonBuilder

@Suppress("DEPRECATION", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object JsonHelper {
    val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
    }

    actual fun <T> T.toJson(): String =
        gson.toJson(this)

    actual inline fun <reified T> fromJson(
        json: String
    ): T = gson.fromJson(json, T::class.java)

}
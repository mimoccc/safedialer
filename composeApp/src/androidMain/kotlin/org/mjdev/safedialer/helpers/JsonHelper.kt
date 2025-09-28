package org.mjdev.safedialer.helpers

import com.google.gson.Gson
import com.google.gson.GsonBuilder

@Suppress("DEPRECATION", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
object JsonHelper {

    // todo may be singleton in di ?
    val gson: Gson by lazy {
        GsonBuilder()
            .setPrettyPrinting()
            .setLenient()
            .serializeNulls()
            .serializeSpecialFloatingPointValues()
            .create()
    }

    fun <T> T.toJson(): String =
        gson.toJson(this)

    inline fun <reified T> fromJson(
        json: String
    ): T = gson.fromJson(json, T::class.java)

}
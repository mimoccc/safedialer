package org.mjdev.safedialer.helpers

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonHelper {
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
package org.mjdev.safedialer.helpers

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object JsonHelper {

    fun <T> T.toJson(): String

    inline fun <reified T> fromJson(json: String): T

}
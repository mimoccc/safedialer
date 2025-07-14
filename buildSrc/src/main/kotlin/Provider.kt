import org.gradle.api.provider.Provider

val Provider<*>.stringValue: String
    get() = runCatching { get().toString() }.getOrNull() ?: ""

val Provider<*>.intValue: Int
    get() = runCatching { stringValue.toInt() }.getOrNull() ?: 0

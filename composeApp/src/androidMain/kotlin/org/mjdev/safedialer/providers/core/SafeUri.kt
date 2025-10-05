package org.mjdev.safedialer.providers.core

import android.net.Uri

inline fun safeUri(
    factory: () -> Uri
): Uri = runCatching { factory() }.getOrElse { Uri.EMPTY }

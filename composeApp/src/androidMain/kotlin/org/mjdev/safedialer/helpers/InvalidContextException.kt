package org.mjdev.safedialer.helpers

import android.content.Context

class InvalidContextException(
    context: Context
) : RuntimeException("Not possible in this scope. Invalid context: ${context::class.simpleName}")
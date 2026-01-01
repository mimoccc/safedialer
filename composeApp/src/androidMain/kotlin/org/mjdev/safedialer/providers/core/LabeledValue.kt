package org.mjdev.safedialer.providers.core

data class LabeledValue(
    val value: String,
    val type: Int,
    val label: String? = null
)
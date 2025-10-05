package org.mjdev.safedialer.providers.core

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FieldMapping(
    val columnName: String,
    val physicalType: PhysicalType,
    val logicalType: LogicalType = LogicalType.Physical,
    val canUpdate: Boolean = false,
    val splitRegex: String = "\\s+"
) {
    enum class PhysicalType {
        String,
        Long,
        Int,
        Double,
        Blob
    }

    enum class LogicalType {
        String,
        Long,
        Int,
        Boolean,
        Array,
        EnumInt,
        Double,
        Physical
    }
}

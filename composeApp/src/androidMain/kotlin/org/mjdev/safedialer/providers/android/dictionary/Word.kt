package org.mjdev.safedialer.providers.android.dictionary

import android.net.Uri
import android.provider.BaseColumns
import android.provider.UserDictionary
import org.mjdev.safedialer.providers.core.Entity
import org.mjdev.safedialer.providers.core.FieldMapping
import org.mjdev.safedialer.providers.core.IgnoreMapping

data class Word(
    @FieldMapping(
        columnName = BaseColumns._ID,
        physicalType = FieldMapping.PhysicalType.Long
    )
    var id: Long = 0L,

    @FieldMapping(
        columnName = UserDictionary.Words.WORD,
        physicalType = FieldMapping.PhysicalType.String
    )
    var word: String? = null,

    @FieldMapping(
        columnName = UserDictionary.Words.FREQUENCY,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var frequency: Int = 0,

    @FieldMapping(
        columnName = UserDictionary.Words.LOCALE,
        physicalType = FieldMapping.PhysicalType.String
    )
    var locale: String? = null,

    @FieldMapping(
        columnName = UserDictionary.Words.APP_ID,
        physicalType = FieldMapping.PhysicalType.Int
    )
    var appId: Int = 0,

    @FieldMapping(
        columnName = UserDictionary.Words.SHORTCUT,
        physicalType = FieldMapping.PhysicalType.String
    )
    var shortcut: String? = null
) : Entity() {
    companion object : CompanionWithUri {
        @IgnoreMapping
        override val uri: Uri = UserDictionary.Words.CONTENT_URI
    }
}

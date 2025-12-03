package org.mjdev.safedialer.providers.android.dictionary

import android.content.Context
import android.net.Uri
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data

class DictionaryProvider(
    context: Context
) : AbstractProvider(context) {
    fun getWords(): Data<Word>? {
        return getContentTableData(Word.uri, Word::class.java)
    }

    override fun getUris(): List<Uri> = listOf(
        Word.uri
    ).distinct().filter { it != Uri.EMPTY }
}

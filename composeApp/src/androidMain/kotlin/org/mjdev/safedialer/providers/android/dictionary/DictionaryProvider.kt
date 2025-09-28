package org.mjdev.safedialer.providers.android.dictionary

import android.content.Context
import org.mjdev.safedialer.providers.core.AbstractProvider
import org.mjdev.safedialer.providers.core.Data

class DictionaryProvider(
    context: Context
) : AbstractProvider(context) {
    fun getWords(): Data<Word>? {
        return getContentTableData(Word.uri, Word::class.java)
    }
}

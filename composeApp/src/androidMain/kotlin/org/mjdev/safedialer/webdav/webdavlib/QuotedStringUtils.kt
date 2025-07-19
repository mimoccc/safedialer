package org.mjdev.safedialer.webdav.webdavlib

object QuotedStringUtils {
    fun asQuotedString(raw: String) =
            "\"" + raw.replace("\\" ,"\\\\").replace("\"", "\\\"") + "\""

    fun decodeQuotedString(quoted: String): String {
        val len = quoted.length
        if (len >= 2 && quoted[0] == '"' && quoted[len-1] == '"') {
            val result = StringBuffer(len)
            var pos = 1
            while (pos < len-1) {
                var c = quoted[pos]
                if (c == '\\' && pos != len-2)
                    c = quoted[++pos]
                result.append(c)
                pos++
            }
            return result.toString()
        } else
            return quoted
    }
}

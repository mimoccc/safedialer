package org.mjdev.safedialer.helpers

import org.bouncycastle.crypto.digests.MD5Digest

object MD5Utils {
    fun computeMd5(data: ByteArray): String {
        val digest = MD5Digest()
        digest.update(data, 0, data.size)
        val out = ByteArray(digest.digestSize)
        digest.doFinal(out, 0)
        return out.joinToString("") { "%02x".format(it) }
    }

    fun computeMd5Bytes(data: ByteArray): ByteArray {
        val digest = MD5Digest()
        digest.update(data, 0, data.size)
        val out = ByteArray(digest.digestSize)
        digest.doFinal(out, 0)
        return out
    }
}

package org.mjdev.safedialer.sync.contact

import android.content.Context
import org.mjdev.safedialer.sync.contact.SyncWorkerContacts
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mjdev.safedialer.helpers.MD5Utils
import java.nio.file.Path
import java.nio.file.Paths

class SyncWorkerContactsTest {

    @Test
    fun testComputeMd5() {
        val input = "hello".toByteArray()
        val expectedMd5 = "5d41402abc4b2a76b9719d911017c592"
        val actualMd5 = MD5Utils.computeMd5(input)
        
        assertEquals(expectedMd5, actualMd5)
    }
}

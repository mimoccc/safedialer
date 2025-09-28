package org.mjdev.safedialer.service.external

import org.mjdev.safedialer.data.list.ListItem

@Suppress("unused")
interface Provider {
    suspend fun getInfo(
        item: ListItem,
    ): Any?
}
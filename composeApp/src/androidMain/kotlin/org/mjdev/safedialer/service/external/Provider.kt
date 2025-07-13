package org.mjdev.safedialer.service.external

import org.mjdev.safedialer.data.list.IListItem

interface Provider {
    suspend fun getInfo(
        item: IListItem,
    ): Any?
}
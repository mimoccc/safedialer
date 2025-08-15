package org.mjdev.safedialer.service.external

import android.content.Context
import com.google.i18n.phonenumbers.PhoneNumberUtil
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.android.closestDI
import org.kodein.di.instance
import org.mjdev.safedialer.data.list.IListItem

@Suppress("unused")
class PhoneLookup(
    private val context: Context
) : DIAware {
    override val di: DI by closestDI(context)
    private val pnu: PhoneNumberUtil by instance()
    private val httpAddressTemplate = "https://mjdev.org/phone/%s"
    private val httpClient by instance<OkHttpClient>()

    @Suppress("PropertyName")
    val HtmlParser: (html: String) -> String? = { html ->
        val regex = "<title>(.*?)</title>".toRegex()
        regex.find(html)?.groupValues?.get(1)?.trim()
    }

    suspend fun getInfo(
        item: IListItem,
        parser: (String) -> String? = HtmlParser,
    ): Any? = runCatching {
        val phone = runCatching { pnu.parse(item.phoneNumber, null) }.getOrNull()
        val url = httpAddressTemplate.format(phone?.nationalNumber?.toString() ?: "")
        val request = Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()
        httpClient.newCall(
            request
        ).execute().let { response ->
            if (response.isSuccessful) {
                parser(response.body.string() ?: "")
            } else {
                Exception("Error: ${response.code}").printStackTrace()
                null
            }
        }
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull()

    suspend fun getInfo(
        phoneNumber: String?,
        parser: (String) -> String? = HtmlParser,
    ): Any? = runCatching {
        val phone = runCatching { pnu.parse(phoneNumber, null) }.getOrNull()
        val url = httpAddressTemplate.format(phone?.nationalNumber?.toString() ?: "")
        val request = Request.Builder()
            .url(url)
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()
        httpClient.newCall(
            request
        ).execute().let { response ->
            if (response.isSuccessful) {
                parser(response.body.string() ?: "")
            } else {
                Exception("Error: ${response.code}").printStackTrace()
                null
            }
        }
    }.onFailure { e ->
        e.printStackTrace()
    }.getOrNull()

    suspend fun updateDetails(
        data: List<IListItem> = listOf(),
        providers: List<Provider> = listOf(),
    ) = runCatching {
        providers.forEach { provider ->
            data.forEach { item ->
                val info = provider.getInfo(item)
//                if (info != null) {
//                    // todo store info
//                }
            }
        }
    }.onFailure { e ->
        e.printStackTrace()
    }
}

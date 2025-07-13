package org.mjdev.safedialer.service.external

import com.google.i18n.phonenumbers.PhoneNumberUtil
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.mjdev.safedialer.data.list.IListItem
import java.util.concurrent.TimeUnit

@Suppress("ConstPropertyName")
object PhoneLookup {
    val pnu: PhoneNumberUtil = PhoneNumberUtil.getInstance()
    private const val httpAddressTemplate = "https://kdomivolal.eu/%s"

    val httpClient by lazy {
        OkHttpClient.Builder()
            .callTimeout(60000, TimeUnit.MILLISECONDS)
            .build()
    }

    val HtmlParser: (html: String) -> String? = { html ->
        val regex = "<title>(.*?)</title>".toRegex()
        regex.find(html)?.groupValues?.get(1)?.trim()
    }

    suspend fun getInfo(
        item: IListItem,
    ): Any? = runCatching {
        val parser: (String) -> String? = HtmlParser
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

    suspend fun updateDetails(
        data: List<IListItem> = listOf(),
        providers: List<Provider> = listOf(),
    ) = runCatching {
        providers.forEach { provider ->
            data.forEach { item ->
                val info = provider.getInfo(item)
                if (info != null) {
                    // todo store info
                }
            }
        }
    }.onFailure { e ->
        e.printStackTrace()
    }

}
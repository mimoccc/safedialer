package org.mjdev.safedialer.webdav

import android.content.Context
import android.util.Log
import ezvcard.Ezvcard
import ezvcard.VCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.webdav.property.webdav.DisplayName
import org.mjdev.safedialer.webdav.property.webdav.ResourceType
import org.mjdev.safedialer.webdav.webdavlib.BasicDigestAuthHandler
import org.mjdev.safedialer.webdav.webdavlib.DavCollection
import org.mjdev.safedialer.webdav.webdavlib.Response

@Suppress("unused", "MemberVisibilityCanBePrivate")
class WebDavClient(
    val context: Context,
    val baseUrl: String = "https://${BuildConfig.SERVER}/webdav/",
    val user: String = BuildConfig.SERVER_UNAME,
    val password: String = BuildConfig.SERVER_UPASS,
    val vcardFileName: String = USER_FILE_VCARD,
    val pgpCertFile: String = USER_FILE_PGP,
) {
    private val client: OkHttpClient
        get() = BasicDigestAuthHandler(
            domain = null,
            username = user,
            password = password.toCharArray()
        ).let { auth ->
            OkHttpClient.Builder()
                .followRedirects(false)
                .authenticator(auth)
                .addNetworkInterceptor(auth)
                .addInterceptor(HttpLoggingInterceptor().apply {
                    setLevel(
                        if (BuildConfig.IS_DEBUG) HttpLoggingInterceptor.Level.HEADERS
                        else HttpLoggingInterceptor.Level.NONE
                    )
                })
                .build()
        }

    val userVCard: Flow<VCard?> = flow {
        readFile(vcardFileName)
            .toString(Charsets.UTF_8)
            .let { text ->
                Ezvcard.parse(text).all().firstOrNull().also { vcard ->
                    emit(vcard)
                }
            }
    }.flowOn(Dispatchers.IO)

    val pgpCertData: Flow<ByteArray> = flow {
        emit(readFile(pgpCertFile))
    }.flowOn(Dispatchers.IO)

    fun listExtended(
        path: String
    ): List<WebDavEntry> = runCatching {
        val base = baseUrl.trimEnd('/')
        val target = if (path.startsWith("http")) path else "$base/${path.trimStart('/')}"
        val collection = DavCollection(
            client,
            target.toHttpUrl()
        )
        val entries = mutableListOf<WebDavEntry>()
        collection.propfind(depth = 1, DisplayName.NAME, ResourceType.NAME) { response, relation ->
            if (relation == Response.HrefRelation.MEMBER) {
                val name = response[DisplayName::class.java]?.displayName
                    ?: response.href.pathSegments.lastOrNull { it.isNotEmpty() }
                    ?: ""
                val isCollection =
                    response[ResourceType::class.java]?.types?.contains(ResourceType.COLLECTION) == true
                entries.add(WebDavEntry(name, response.href.toString(), isCollection))
            }
        }
        entries
    }.onFailure { e ->
        Log.e(TAG, e.message ?: "")
    }.getOrNull() ?: emptyList()

    fun list(
        path: String
    ): List<String> = runCatching {
        val base = baseUrl.trimEnd('/')
        val target = if (path.startsWith("http")) path else "$base/${path.trimStart('/')}"
        val collection = DavCollection(
            client,
            target.toHttpUrl()
        )
        val names = mutableListOf<String>()
        collection.propfind(depth = 1, DisplayName.NAME) { response, _ ->
            response[DisplayName::class.java]?.displayName?.let { names.add(it) }
        }
        names
    }.onFailure { e ->
        Log.e(TAG, e.message ?: "")
    }.getOrNull() ?: emptyList()

    fun readFile(
        filePath: String
    ): ByteArray = runCatching {
        val base = baseUrl.trimEnd('/')
        val target = if (filePath.startsWith("http")) filePath
        else "$base/${filePath.trimStart('/')}"
        val file = DavCollection(client, target.toHttpUrl())
        file.get(accept = "*/*", headers = null).use { response ->
            return response.body.bytes()
        }
    }.onFailure { e ->
        Log.e(TAG, e.message ?: "")
    }.getOrNull() ?: ByteArray(0)

    fun putFile(
        filePath: String,
        data: ByteArray,
        contentType: String // = "text/vcard; charset=utf-8"
    ) = runCatching {
        val base = baseUrl.trimEnd('/')
        val target =
            if (filePath.startsWith("http")) filePath else "$base/${filePath.trimStart('/')}"
        val request = Request.Builder()
            .url(target)
            .put(data.toRequestBody(contentType.toMediaType()))
            .build()
        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IllegalStateException("PUT failed ${resp.code}")
        }
    }.onFailure { e ->
        Log.e(TAG, e.message ?: "")
    }

    fun delete(
        path: String
    ) = runCatching {
        val base = baseUrl.trimEnd('/')
        val target = if (path.startsWith("http")) path else "$base/${path.trimStart('/')}"
        val request = Request.Builder()
            .url(target)
            .delete()
            .build()
        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IllegalStateException("DELETE failed ${resp.code}")
        }
    }.onFailure { e ->
        Log.e(TAG, e.message ?: "")
    }

    fun move(
        sourcePath: String,
        destinationPath: String,
        overwrite: Boolean = true
    ) = runCatching {
        val base = baseUrl.trimEnd('/')
        val source =
            if (sourcePath.startsWith("http")) sourcePath else "$base/${sourcePath.trimStart('/')}"
        val destination = if (destinationPath.startsWith("http")) destinationPath else "$base/${
            destinationPath.trimStart('/')
        }"
        val request = Request.Builder()
            .url(source)
            .method("MOVE", null)
            .header("Destination", destination)
            .header("Overwrite", if (overwrite) "T" else "F")
            .build()
        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IllegalStateException("MOVE failed ${resp.code}")
        }
    }.onFailure { e ->
        Log.e(TAG, e.message ?: "")
    }

    fun mkcol(
        path: String
    ) = runCatching {
        val base = baseUrl.trimEnd('/')
        val target = if (path.startsWith("http")) path else "$base/${path.trimStart('/')}"
        val collection = DavCollection(client, target.toHttpUrl())
        collection.mkCol(null) { resp ->
            if (!resp.isSuccessful && resp.code != 405) { // 405 Method Not Allowed - often means already exists
                throw IllegalStateException("MKCOL failed ${resp.code}")
            }
        }
    }.onFailure { e ->
        Log.e(TAG, e.message ?: "")
    }

    fun getLastModified(
        remotePath: String
    ): Long? = runCatching {
        val base = baseUrl.trimEnd('/')
        val target = if (remotePath.startsWith("http")) remotePath
        else "$base/${remotePath.trimStart('/')}"
        val request = Request.Builder()
            .url(target)
            .head()
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) {
                Log.w(TAG, "HEAD request failed with code ${response.code}")
                return@use null
            }
            response.header("Last-Modified")?.let { lastModified ->
                java.text.SimpleDateFormat(
                    "EEE, dd MMM yyyy HH:mm:ss zzz",
                    java.util.Locale.US
                ).parse(lastModified)?.time
            }
        }
    }.onFailure { e ->
        Log.e(TAG, "Failed to get last modified: ${e.message}")
    }.getOrNull()

    companion object {
        val TAG = WebDavClient::class.simpleName

        const val DIR_AI_HISTORY = ".ai_history"
        const val DIR_AUTHENTICATOR = ".authenticator"

        const val DIR_INVOICES = "Invoices"
        const val DIR_CONTACTS = "Contacts"
        const val DIR_CALL_LOG = "CallLog"
        const val DIR_TASKS = "Tasks"
        const val DIR_CALENDAR = "Calendar"
        const val DIR_MESSAGES = "Messages"
        const val DIR_NOTES = "Notes"
        const val DIR_GALLERY = "Pictures"

        // todo
        const val DIR_DOCUMENTS = "Documents"
        const val DIR_MUSIC = "Music"
        const val DIR_VIDEOS = "Videos"
        const val DIR_PRESENTATIONS = "Presentations"
        const val DIR_PUBLIC = "Public"
        const val DIR_REPOSITORIES = "Repositories"
        const val DIR_WEB = "Web"

        const val DIR_IMAP = ".mail/imap"
        const val DIR_SMTP = ".mail/smtp"

        const val USER_FILE_PGP = "pgp_mail_cert.asc"
        const val USER_FILE_VCARD = "vcard.vcf"
    }
}

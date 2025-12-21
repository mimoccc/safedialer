package org.mjdev.safedialer.webdav

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import ezvcard.Ezvcard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DIAware
import org.mjdev.safedialer.BuildConfig
import org.mjdev.safedialer.webdav.property.webdav.DisplayName
import org.mjdev.safedialer.webdav.property.webdav.ResourceType
import org.mjdev.safedialer.webdav.webdavlib.BasicDigestAuthHandler
import org.mjdev.safedialer.webdav.webdavlib.DavCollection
import org.mjdev.safedialer.webdav.webdavlib.Response

@Suppress("unused", "RemoveExplicitTypeArguments", "MemberVisibilityCanBePrivate")
class WebDavClient(
    val context: Context,
    val baseUrl: String = "https://${BuildConfig.SERVER}/webdav/",
    val user: String = BuildConfig.SERVER_UNAME,
    val password: String = BuildConfig.SERVER_UPASS,
    val vcardFileName: String = USER_FILE_VCARD,
    val pgpCertFile: String = USER_FILE_PGP,
) : DIAware {
    override val di by lazy {
        (context as DIAware).di
    }

    // todo DI
    private val auth: BasicDigestAuthHandler
        get() = BasicDigestAuthHandler(
            domain = null,
            username = user,
            password = password.toCharArray()
        )

    // todo DI
    private val client: OkHttpClient
        get() = OkHttpClient.Builder()
            .followRedirects(false)
            .authenticator(auth)
            .addNetworkInterceptor(auth)
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(
                    if (BuildConfig.IS_DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE
                )
            })
            .build()

    private val userVCard by lazy {
        readFile(vcardFileName)
            .toString(Charsets.UTF_8)
            .let { text ->
                Ezvcard.parse(text).all().firstOrNull()
            }
    }

    val pgpCertData: ByteArray by lazy {
        readFile(pgpCertFile)
    }

    val userPicture = flow<ImageBitmap?> {
        userVCard?.photos?.firstOrNull()?.data?.let { photoData ->
            BitmapFactory.decodeByteArray(
                photoData,
                0,
                photoData.size
            )?.asImageBitmap()?.let { bmp ->
                emit(bmp)
            }
        }
    }.flowOn(Dispatchers.IO)

    val allEmails = flow<Map<String, ByteArray>> {
        Log.d(TAG, "Getting imap folders")
        list(DIR_IMAP).apply {
            Log.d(TAG, "Got imap folders: $this")
        }.map { mFolder ->
            Log.d(TAG, "Getting files in: $mFolder")
            list("$DIR_IMAP/$mFolder").map { mailFile ->
                val mailPath = "$DIR_IMAP/$mFolder/$mailFile"
                Log.d(TAG, "Got file: $mailPath")
                val emailData = readFile(mailPath)
                Log.d(TAG, "File size: ${emailData.size}")
                mailPath to emailData
            } ?: emptyList()
        }.flatten().toMap().let { emails ->
            Log.d(TAG, "Got ${emails.size} emails.")
            emit(emails)
        }
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

    data class WebDavEntry(
        val name: String,
        val fullUrl: String,
        val isCollection: Boolean
    )

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

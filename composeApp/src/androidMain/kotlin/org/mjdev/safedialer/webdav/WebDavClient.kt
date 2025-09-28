package org.mjdev.safedialer.webdav

import org.mjdev.safedialer.webdav.webdavlib.DavCollection
import org.mjdev.safedialer.webdav.property.webdav.DisplayName
import ezvcard.Ezvcard
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.mjdev.safedialer.BuildConfig
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mjdev.safedialer.webdav.webdavlib.BasicDigestAuthHandler

@Suppress("unused")
class WebDavClient(
    val url: String = "https://${BuildConfig.SERVER}/webdav/",
    val user: String = BuildConfig.SERVER_UNAME,
    val password: String = BuildConfig.SERVER_UPASS,
    val vcardFileName: String = USER_FILE_VCARD,
    val pgpCertFile: String = USER_FILE_PGP,
) {
    private val auth: BasicDigestAuthHandler by lazy {
        BasicDigestAuthHandler(
            domain = null,
            username = user,
            password = password.toCharArray()
        )
    }

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .followRedirects(false)
            .authenticator(auth)
            .addNetworkInterceptor(auth)
            .build()
    }

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

    fun list(
        path: String
    ): List<String> {
        val base = url.trimEnd('/')
        val target = if (path.startsWith("http")) path else "$base/${path.trimStart('/')}"
        val collection = DavCollection(
            client,
            target.toHttpUrl()
        )
        val names = mutableListOf<String>()
        collection.propfind(depth = 1, DisplayName.NAME) { response, _ ->
            response[DisplayName::class.java]?.displayName?.let { names.add(it) }
        }
        return names
    }

    fun readFile(
        filePath: String
    ): ByteArray {
        val base = url.trimEnd('/')
        val target = if (filePath.startsWith("http")) filePath
        else "$base/${filePath.trimStart('/')}"
        val file = DavCollection(client, target.toHttpUrl())
        file.get(accept = "*/*", headers = null).use { response ->
            return response.body.bytes()
        }
    }

    fun putFile(
        filePath: String,
        data: ByteArray,
        contentType: String = "text/vcard; charset=utf-8"
    ) {
        val base = url.trimEnd('/')
        val target =
            if (filePath.startsWith("http")) filePath else "$base/${filePath.trimStart('/')}"
        val request = Request.Builder()
            .url(target)
            .put(data.toRequestBody(contentType.toMediaType()))
            .build()
        client.newCall(request).execute().use { resp ->
            if (!resp.isSuccessful) throw IllegalStateException("PUT failed ${resp.code}")
        }
    }

    fun move(
        sourcePath: String,
        destinationPath: String,
        overwrite: Boolean = true
    ) {
        val base = url.trimEnd('/')
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
    }

    companion object {
        const val DIR_CONTACTS = "Contacts"
        const val DIR_CALL_LOG = "CallLog"
        const val DIR_TASKS = "Tasks"
        const val DIR_CALENDAR = "Calendar"
        const val DIR_GALLERY = "Pictures/DCIM"

        const val USER_FILE_PGP = "pgp_mail_cert.asc"
        const val USER_FILE_VCARD = "vcard.vcf"
    }
}

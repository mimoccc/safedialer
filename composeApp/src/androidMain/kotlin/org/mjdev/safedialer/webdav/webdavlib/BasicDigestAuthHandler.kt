package org.mjdev.safedialer.webdav.webdavlib

import okhttp3.Authenticator
import okhttp3.Challenge
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.Route
import okio.Buffer
import okio.ByteString.Companion.toByteString
import java.io.IOException
import java.util.LinkedList
import java.util.Locale
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Logger

class BasicDigestAuthHandler(
    val domain: String?,
    val username: String,
    val password: CharArray,
    val insecurePreemptive: Boolean = false
): Authenticator, Interceptor {
    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"

        var clientNonce = h(UUID.randomUUID().toString())
        var nonceCount = AtomicInteger(1)

        fun quotedString(s: String) = "\"" + s.replace("\"", "\\\"") + "\""
        fun h(data: String) = data.toByteArray().toByteString().md5().hex()

        fun h(body: RequestBody): String {
            val buffer = Buffer()
            body.writeTo(buffer)
            return buffer.readByteArray().toByteString().md5().hex()
        }

        fun kd(secret: String, data: String) = h("$secret:$data")
    }

    private var basicAuth: Challenge? = null
    private var digestAuth: Challenge? = null
    private val logger = Logger.getLogger(javaClass.name)

    fun authenticateRequest(request: Request, response: Response?): Request? {
        domain?.let {
            val host = request.url.host
            if (!domain.equals(UrlUtils.hostToDomain(host), true)) {
                logger.warning("Not authenticating against $host because it doesn't belong to $domain")
                return null
            }
        }
        if (response == null) {
            if (basicAuth == null && digestAuth == null && (request.isHttps || insecurePreemptive)) {
                logger.fine("Trying Basic auth preemptively")
                basicAuth = Challenge("Basic", "")
            }
        } else {
            var newBasicAuth: Challenge? = null
            var newDigestAuth: Challenge? = null
            for (challenge in response.challenges())
                when {
                    "Basic".equals(challenge.scheme, true) -> {
                        basicAuth?.let {
                            logger.warning("Basic credentials didn't work last time -> aborting")
                            basicAuth = null
                            return null
                        }
                        newBasicAuth = challenge
                    }
                    "Digest".equals(challenge.scheme, true) -> {
                        if (digestAuth != null && !"true".equals(challenge.authParams["stale"], true)) {
                            logger.warning("Digest credentials didn't work last time and server nonce has not expired -> aborting")
                            digestAuth = null
                            return null
                        }
                        newDigestAuth = challenge
                    }
                }
            basicAuth = newBasicAuth
            digestAuth = newDigestAuth
        }
        when {
            digestAuth != null -> {
                logger.fine("Adding Digest authorization request for ${request.url}")
                return digestRequest(request, digestAuth)
            }
            basicAuth != null -> {
                logger.fine("Adding Basic authorization header for ${request.url}")
                return request.newBuilder()
                        .header(HEADER_AUTHORIZATION, Credentials.basic(username, password.concatToString(), Charsets.UTF_8))
                        .build()
            }
            response != null ->
                logger.warning("No supported authentication scheme")
        }
        return null
    }

    fun digestRequest(request: Request, digest: Challenge?): Request? {
        if (digest == null)
            return null
        val realm = digest.authParams["realm"]
        val opaque = digest.authParams["opaque"]
        val nonce = digest.authParams["nonce"]
        val algorithm = Algorithm.determine(digest.authParams["algorithm"])
        val qop = Protection.selectFrom(digest.authParams["qop"])
        var response: String? = null
        val params = LinkedList<String>()
        params.add("username=${quotedString(username)}")
        if (realm != null)
            params.add("realm=${quotedString(realm)}")
        else {
            logger.warning("No realm provided, aborting Digest auth")
            return null
        }
        if (nonce != null)
            params.add("nonce=${quotedString(nonce)}")
        else {
            logger.warning("No nonce provided, aborting Digest auth")
            return null
        }
        if (opaque != null)
            params.add("opaque=${quotedString(opaque)}")
        if (algorithm != null)
            params.add("algorithm=${quotedString(algorithm.algorithm)}")
        val method = request.method
        val digestURI = request.url.encodedPath
        params.add("uri=${quotedString(digestURI)}")
        if (qop != null) {
            params.add("qop=${qop.qop}")
            params.add("cnonce=${quotedString(clientNonce)}")
            val nc = nonceCount.getAndIncrement()
            val ncValue = String.Companion.format(Locale.ROOT, "%08x", nc)
            params.add("nc=$ncValue")
            val a1: String? = when (algorithm) {
                Algorithm.MD5 ->
                    "$username:$realm:${password.concatToString()}"
                Algorithm.MD5_SESSION ->
                    h("$username:$realm:${password.concatToString()}") + ":$nonce:$clientNonce"
                else ->
                    null
            }
            val a2: String? = when (qop) {
                Protection.Auth ->
                    "$method:$digestURI"
                Protection.AuthInt -> {
                    try {
                        val body = request.body
                        "$method:$digestURI:" + (if (body != null) h(body) else h(""))
                    } catch(e: IOException) {
                        logger.warning("Couldn't get entity-body for hash calculation")
                        null
                    }
                }
            }
            if (a1 != null && a2 != null)
                response = kd(h(a1), "$nonce:$ncValue:$clientNonce:${qop.qop}:${h(a2)}")
        } else {
            logger.finer("Using legacy Digest auth")
            if (algorithm == Algorithm.MD5) {
                val a1 = "$username:$realm:${password.concatToString()}"
                val a2 = "$method:$digestURI"
                response = kd(h(a1), nonce + ":" + h(a2))
            }
        }
        return if (response != null) {
            params.add("response=" + quotedString(response))
            request.newBuilder()
                    .header(HEADER_AUTHORIZATION, "Digest " + params.joinToString(", "))
                    .build()
        } else
            null
    }

    private enum class Algorithm(
        val algorithm: String
    ) {
        MD5("MD5"),
        MD5_SESSION("MD5-sess");
        companion object {
            fun determine(paramValue: String?): Algorithm? {
                return when {
                    paramValue == null || MD5.algorithm.equals(paramValue, true) ->
                        MD5
                    MD5_SESSION.algorithm.equals(paramValue, true) ->
                        MD5_SESSION
                    else -> {
                        val logger = Logger.getLogger(Algorithm::javaClass.name)
                        logger.warning("Ignoring unknown hash algorithm: $paramValue")
                        null
                    }
                }
            }
        }
    }

    private enum class Protection(
        val qop: String
    ) {
        Auth("auth"),
        AuthInt("auth-int");

        companion object {
            fun selectFrom(paramValue: String?): Protection? {
                paramValue?.let {
                    var qopAuth = false
                    var qopAuthInt = false
                    for (qop in paramValue.split(","))
                        when (qop) {
                            "auth" -> qopAuth = true
                            "auth-int" -> qopAuthInt = true
                        }
                    if (qopAuthInt)
                        return AuthInt
                    else if (qopAuth)
                        return Auth
                }
                return null
            }
        }
    }

    override fun authenticate(route: Route?, response: Response) =
            authenticateRequest(response.request, response)

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        if (request.header(HEADER_AUTHORIZATION) == null) {
            val authRequest = authenticateRequest(request, null)
            if (authRequest != null)
                request = authRequest
        }
        return chain.proceed(request)
    }
}

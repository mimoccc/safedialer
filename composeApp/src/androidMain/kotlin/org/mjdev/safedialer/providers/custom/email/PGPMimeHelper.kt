package org.mjdev.safedialer.providers.custom.email

import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.jcajce.*
import jakarta.mail.*
import jakarta.mail.internet.*
import java.io.*
import java.security.Security
import java.util.*

object PGPMimeHelper {
    init {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(BouncyCastleProvider())
        }
    }

    fun encryptMimeMessage(
        originalMessage: MimeMessage,
        recipientPublicKeys: List<ByteArray>
    ): MimeMessage {
        val session = originalMessage.session
        val encryptedMessage = MimeMessage(session)
        encryptedMessage.setFrom(originalMessage.from.first())
        encryptedMessage.setRecipients(
            Message.RecipientType.TO,
            originalMessage.getRecipients(Message.RecipientType.TO)
        )
        encryptedMessage.subject = originalMessage.subject
        val multipart = MimeMultipart("encrypted; protocol=\"application/pgp-encrypted\"")
        val versionPart = MimeBodyPart().apply {
            setContent("Version: 1", "application/pgp-encrypted")
            setHeader("Content-Description", "PGP/MIME version identification")
        }
        multipart.addBodyPart(versionPart)
        val originalBytes = ByteArrayOutputStream().apply {
            originalMessage.writeTo(this)
        }.toByteArray()
        val encryptedData = encryptData(originalBytes, recipientPublicKeys)
        val encryptedPart = MimeBodyPart().apply {
            setContent(encryptedData, "application/octet-stream; name=\"encrypted.asc\"")
            setHeader("Content-Description", "OpenPGP encrypted message")
            setHeader("Content-Disposition", "inline; filename=\"encrypted.asc\"")
        }
        multipart.addBodyPart(encryptedPart)
        encryptedMessage.setContent(multipart)
        encryptedMessage.saveChanges()
        return encryptedMessage
    }

    fun decryptMimeMessage(
        encryptedMessage: MimeMessage,
        privateKeyData: ByteArray,
        password: String
    ): MimeMessage {
        val content = encryptedMessage.content
        if (content !is MimeMultipart) {
            throw IllegalArgumentException("Not a PGP/MIME encrypted message")
        }
        var encryptedPart: BodyPart? = null
        for (i in 0 until content.count) {
            val part = content.getBodyPart(i)
            if (part.contentType.contains("application/octet-stream") ||
                part.contentType.contains("application/pgp-encrypted")) {
                val disposition = part.disposition
                if (disposition == null || disposition == Part.INLINE) {
                    encryptedPart = part
                    break
                }
            }
        }
        if (encryptedPart == null) {
            throw IllegalArgumentException("No encrypted part found")
        }
        val encryptedBytes = encryptedPart.inputStream.readBytes()
        val decryptedBytes = decryptData(encryptedBytes, privateKeyData, password)
        val session = encryptedMessage.session
        return MimeMessage(session, ByteArrayInputStream(decryptedBytes))
    }

    fun signMimeMessage(
        originalMessage: MimeMessage,
        privateKeyData: ByteArray,
        password: String
    ): MimeMessage {
        val session = originalMessage.session
        val signedMessage = MimeMessage(session)
        signedMessage.setFrom(originalMessage.from.first())
        signedMessage.setRecipients(
            Message.RecipientType.TO,
            originalMessage.getRecipients(Message.RecipientType.TO)
        )
        signedMessage.subject = originalMessage.subject
        val multipart = MimeMultipart("signed; protocol=\"application/pgp-signature\"; micalg=\"pgp-sha512\"")
        val contentPart = MimeBodyPart().apply {
            setContent(originalMessage.content, originalMessage.contentType)
        }
        multipart.addBodyPart(contentPart)
        val contentBytes = ByteArrayOutputStream().apply {
            contentPart.writeTo(this)
        }.toByteArray()
        val signature = signData(contentBytes, privateKeyData, password)
        val signaturePart = MimeBodyPart().apply {
            setContent(signature, "application/pgp-signature; name=\"signature.asc\"")
            setHeader("Content-Description", "OpenPGP digital signature")
            setHeader("Content-Disposition", "attachment; filename=\"signature.asc\"")
        }
        multipart.addBodyPart(signaturePart)
        signedMessage.setContent(multipart)
        signedMessage.saveChanges()
        return signedMessage
    }

    fun verifySignedMimeMessage(
        signedMessage: MimeMessage,
        senderPublicKey: ByteArray
    ): Boolean {
        val content = signedMessage.content
        if (content !is MimeMultipart) return false
        if (content.count < 2) return false
        val contentPart = content.getBodyPart(0)
        val signaturePart = content.getBodyPart(1)
        val contentBytes = ByteArrayOutputStream().apply {
            contentPart.writeTo(this)
        }.toByteArray()
        val signatureBytes = signaturePart.inputStream.readBytes()
        return verifySignature(contentBytes, signatureBytes, senderPublicKey)
    }

    private fun encryptData(
        data: ByteArray,
        recipientPublicKeys: List<ByteArray>
    ): String {
        val out = ByteArrayOutputStream()
        val armoredOut = ArmoredOutputStream(out)
        val encGen = PGPEncryptedDataGenerator(
            JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                .setWithIntegrityPacket(true)
                .setSecureRandom(java.security.SecureRandom())
                .setProvider("BC")
        )
        recipientPublicKeys.forEach { keyData ->
            val publicKey = readPublicKey(ByteArrayInputStream(keyData))
            encGen.addMethod(JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"))
        }
        val encryptedOut = encGen.open(armoredOut, ByteArray(4096))
        val compressedDataGenerator = PGPCompressedDataGenerator(PGPCompressedData.ZIP)
        val compressedOut = compressedDataGenerator.open(encryptedOut)
        val literalDataGenerator = PGPLiteralDataGenerator()
        val literalOut = literalDataGenerator.open(
            compressedOut,
            PGPLiteralData.BINARY,
            "_CONSOLE",
            data.size.toLong(),
            Date()
        )
        literalOut.write(data)
        literalOut.close()
        compressedOut.close()
        encryptedOut.close()
        armoredOut.close()
        return out.toString("UTF-8")
    }

    private fun decryptData(
        encryptedData: ByteArray,
        privateKeyData: ByteArray,
        password: String
    ): ByteArray {
        val keyIn = PGPUtil.getDecoderStream(ByteArrayInputStream(encryptedData))
        val pgpF = PGPObjectFactory(keyIn, JcaKeyFingerprintCalculator())
        val encList = pgpF.nextObject() as PGPEncryptedDataList
        val secretKey = readSecretKey(ByteArrayInputStream(privateKeyData))
        val privateKey = secretKey.extractPrivateKey(
            JcePBESecretKeyDecryptorBuilder()
                .setProvider("BC")
                .build(password.toCharArray())
        )
        var pbe: PGPPublicKeyEncryptedData? = null
        encList.encryptedDataObjects.forEach { obj ->
            val candidate = obj as PGPPublicKeyEncryptedData
            if (secretKey.keyID == candidate.keyID) {
                pbe = candidate
            }
        }
        if (pbe == null) {
            throw IllegalStateException("No matching encrypted data found")
        }
        val clear = pbe!!.getDataStream(
            JcePublicKeyDataDecryptorFactoryBuilder()
                .setProvider("BC")
                .build(privateKey)
        )
        val plainFact = PGPObjectFactory(clear, JcaKeyFingerprintCalculator())
        var obj = plainFact.nextObject()
        if (obj is PGPCompressedData) {
            val compressedFact = PGPObjectFactory(
                obj.dataStream,
                JcaKeyFingerprintCalculator()
            )
            obj = compressedFact.nextObject()
        }
        val literalData = obj as PGPLiteralData
        return literalData.inputStream.readBytes()
    }

    private fun signData(
        data: ByteArray,
        privateKeyData: ByteArray,
        password: String
    ): String {
        val secretKey = readSecretKey(ByteArrayInputStream(privateKeyData))
        val privateKey = secretKey.extractPrivateKey(
            JcePBESecretKeyDecryptorBuilder()
                .setProvider("BC")
                .build(password.toCharArray())
        )
        val signatureGenerator = PGPSignatureGenerator(
            JcaPGPContentSignerBuilder(
                secretKey.publicKey.algorithm,
                PGPUtil.SHA512
            ).setProvider("BC")
        )
        signatureGenerator.init(PGPSignature.BINARY_DOCUMENT, privateKey)
        val out = ByteArrayOutputStream()
        val armoredOut = ArmoredOutputStream(out)
        signatureGenerator.update(data)
        val signature = signatureGenerator.generate()
        signature.encode(armoredOut)
        armoredOut.close()
        return out.toString("UTF-8")
    }

    private fun verifySignature(
        data: ByteArray,
        signatureData: ByteArray,
        publicKeyData: ByteArray
    ): Boolean = runCatching {
        val sigIn = PGPUtil.getDecoderStream(ByteArrayInputStream(signatureData))
        val pgpFact = PGPObjectFactory(sigIn, JcaKeyFingerprintCalculator())
        val signatureList = pgpFact.nextObject() as PGPSignatureList
        val publicKey = readPublicKey(ByteArrayInputStream(publicKeyData))
        val signature = signatureList[0]
        signature.init(JcaPGPContentVerifierBuilderProvider().setProvider("BC"), publicKey)
        signature.update(data)
        signature.verify()
    }.getOrDefault(false)

    private fun readPublicKey(keyIn: InputStream): PGPPublicKey {
        val pgpPub = PGPPublicKeyRingCollection(
            PGPUtil.getDecoderStream(keyIn),
            JcaKeyFingerprintCalculator()
        )
        pgpPub.keyRings.forEach { keyRing ->
            keyRing.publicKeys.forEach { key ->
                if (key.isEncryptionKey) return key
            }
        }
        throw IllegalArgumentException("No encryption key found")
    }

    private fun readSecretKey(keyIn: InputStream): PGPSecretKey {
        val pgpSec = PGPSecretKeyRingCollection(
            PGPUtil.getDecoderStream(keyIn),
            JcaKeyFingerprintCalculator()
        )
        pgpSec.keyRings.forEach { keyRing ->
            keyRing.secretKeys.forEach { key ->
                if (key.isSigningKey) return key
            }
        }
        throw IllegalArgumentException("No signing key found")
    }
}

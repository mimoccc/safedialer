import org.bouncycastle.bcpg.ArmoredOutputStream
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.bouncycastle.openpgp.*
import org.bouncycastle.openpgp.operator.jcajce.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.security.Security

object PGPHelper {
    init {
        Security.addProvider(BouncyCastleProvider())
    }

    fun encryptMessage(
        plainText: String,
        privateKeyData: ByteArray
    ): String {
        val publicKey = extractPublicKeyFromPrivate(privateKeyData)
        val out = ByteArrayOutputStream()
        val armoredOut = ArmoredOutputStream(out)
        val encGen = PGPEncryptedDataGenerator(
            JcePGPDataEncryptorBuilder(PGPEncryptedData.AES_256)
                .setWithIntegrityPacket(true)
                .setSecureRandom(java.security.SecureRandom())
                .setProvider("BC")
        )
        encGen.addMethod(JcePublicKeyKeyEncryptionMethodGenerator(publicKey).setProvider("BC"))
        val encryptedOut = encGen.open(armoredOut, ByteArray(4096))
        val literalData = PGPLiteralDataGenerator()
        val literalOut = literalData.open(
            encryptedOut,
            PGPLiteralData.BINARY,
            "_CONSOLE",
            plainText.length.toLong(),
            java.util.Date()
        )
        literalOut.write(plainText.toByteArray())
        literalOut.close()
        encryptedOut.close()
        armoredOut.close()
        return out.toString("UTF-8")
    }

    fun decryptMessage(
        encryptedText: String,
        privateKeyData: ByteArray,
        password: String
    ): String {
        val keyIn = ByteArrayInputStream(privateKeyData)
        val secretKey = readSecretKey(keyIn)
        val encIn = PGPUtil.getDecoderStream(ByteArrayInputStream(encryptedText.toByteArray()))
        val pgpF = PGPObjectFactory(encIn, JcaKeyFingerprintCalculator())
        val enc = pgpF.nextObject() as PGPEncryptedDataList
        val pbe = enc.encryptedDataObjects.next() as PGPPublicKeyEncryptedData
        val privateKey = secretKey.extractPrivateKey(
            JcePBESecretKeyDecryptorBuilder()
                .setProvider("BC")
                .build(password.toCharArray())
        )
        val clear = pbe.getDataStream(
            JcePublicKeyDataDecryptorFactoryBuilder()
                .setProvider("BC")
                .build(privateKey)
        )
        val plainFact = PGPObjectFactory(clear, JcaKeyFingerprintCalculator())
        val message = plainFact.nextObject() as PGPLiteralData
        return message.inputStream.readBytes().toString(Charsets.UTF_8)
    }

    private fun extractPublicKeyFromPrivate(privateKeyData: ByteArray): PGPPublicKey {
        val keyIn = ByteArrayInputStream(privateKeyData)
        val pgpSec = PGPSecretKeyRingCollection(
            PGPUtil.getDecoderStream(keyIn),
            JcaKeyFingerprintCalculator()
        )
        pgpSec.keyRings.forEach { keyRing ->
            keyRing.secretKeys.forEach { secretKey ->
                if (secretKey.isSigningKey) {
                    return secretKey.publicKey
                }
            }
        }
        throw IllegalArgumentException("No signing key found in private key")
    }

    private fun readSecretKey(keyIn: java.io.InputStream): PGPSecretKey {
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

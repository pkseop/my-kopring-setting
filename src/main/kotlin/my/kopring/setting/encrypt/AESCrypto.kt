package my.kopring.setting.encrypt

import org.apache.commons.codec.binary.Base64
import java.io.UnsupportedEncodingException
import java.security.spec.AlgorithmParameterSpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESCrypto {

    private const val CLOUD_KEY = "r7bni384#8!0*fjk"
    private val IV = ByteArray(16)


    fun encrypt(source: String): String {
        return encrypt(source, CLOUD_KEY)
    }

    fun decrypt(source: String): String {
        return decrypt(source, CLOUD_KEY)
    }

    private fun encrypt(source: String, key: String): String {
        return try {
            String(Base64.encodeBase64(encrypt(source.toByteArray(), key.toByteArray())), Charsets.UTF_8)
        } catch (ex: UnsupportedEncodingException) {
            throw RuntimeException(ex)
        }
    }

    private fun decrypt(source: String, key: String): String {
        return try {
            String(decrypt(Base64.decodeBase64(source.toByteArray()), key.toByteArray()), Charsets.UTF_8)
        } catch (ex: UnsupportedEncodingException) {
            throw RuntimeException(ex)
        }
    }

    private fun encrypt(source: ByteArray, key: ByteArray): ByteArray {
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(IV)
        val newKey = SecretKeySpec(key, "AES")
        return try {
            var cipher: Cipher? = null
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec)
            cipher.doFinal(source)
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }

    private fun decrypt(source: ByteArray, key: ByteArray): ByteArray {
        val ivSpec: AlgorithmParameterSpec = IvParameterSpec(IV)
        val newKey = SecretKeySpec(key, "AES")
        return try {
            val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec)
            cipher.doFinal(source)
        } catch (ex: Exception) {
            throw RuntimeException(ex)
        }
    }
}
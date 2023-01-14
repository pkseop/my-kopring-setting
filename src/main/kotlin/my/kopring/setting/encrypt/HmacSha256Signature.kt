package my.kopring.setting.encrypt

import java.util.Formatter
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HmacSha256Signature {
    private const val HMAC_SHA_ALGORITHM = "HmacSHA256"

    private fun toHexString(bytes: ByteArray): String {
        Formatter().use { formatter ->
            for (b in bytes) {
                formatter.format("%02x", b)
            }
            return formatter.toString()
        }
    }

    @Throws(Exception::class)
    fun digest(data: String, key: String): String {
        val signingKey = SecretKeySpec(key.toByteArray(), HMAC_SHA_ALGORITHM)
        val mac = Mac.getInstance(HMAC_SHA_ALGORITHM)
        mac.init(signingKey)
        return toHexString(mac.doFinal(data.toByteArray()))
    }

    fun generateFingerprint(serviceId: String, timestamp: Long, secureKey: String): String? {
        val data = "$serviceId;$timestamp"
        return try {
            digest(data, secureKey)
        } catch (e: Exception) {
            null
        }
    }
}
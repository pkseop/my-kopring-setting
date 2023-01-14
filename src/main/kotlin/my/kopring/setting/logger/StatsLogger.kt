package my.kopring.setting.logger

import mu.KotlinLogging
import my.kopring.setting.collect.model.PingContentStats
import my.kopring.setting.encrypt.HmacSha256Signature
import my.kopring.setting.enums.StatsLogType
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SignatureException

private val log = KotlinLogging.logger {}

@Component
class StatsLogger(
    @Value("\${user.hash.secret}")
    private val userHashSecret: String,

    private val statsPingLogger: StatsPingLogger,
    private val statsContentLogger: StatsContentLogger
) {

    @Async("threadPoolTaskExecutor")
    fun pingContent(stats: PingContentStats) {
        try {
            stats.logType = StatsLogType.PING_CONTENT
            statsPingLogger.log(stats)
        } catch(e: Exception) {
            log.warn("Fail pingContent")
        }
    }

    private fun generateUserHash(serviceId: String, userId: String): String? {
        var generatedFingerprint: String? = null
        try {
            generatedFingerprint = HmacSha256Signature.digest(
                String.format("%s;%s", serviceId, userId),
                userHashSecret
            )
        } catch (e: InvalidKeyException) {
            e.printStackTrace()
        } catch (e: SignatureException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return generatedFingerprint
    }
}
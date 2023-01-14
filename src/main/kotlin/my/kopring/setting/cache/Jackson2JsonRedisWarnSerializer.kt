package my.kopring.setting.cache

import mu.KotlinLogging
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer

private val log = KotlinLogging.logger {}

class Jackson2JsonRedisWarnSerializer : GenericJackson2JsonRedisSerializer() {

    override fun serialize(source: Any?): ByteArray {
        try {
            return super.serialize(source)
        } catch(e: Exception) {
            log.warn(e.message, e)
        }
        return byteArrayOf(0);
    }

    override fun <T : Any?> deserialize(source: ByteArray?, type: Class<T>): T? {
        try {
            return super.deserialize(source, type)
        } catch(e: Exception) {
            log.warn(e.message, e)
        }
        return null;
    }

}
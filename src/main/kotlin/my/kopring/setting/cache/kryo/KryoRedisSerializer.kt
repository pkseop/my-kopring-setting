package biz.gripcloud.api.cache.kryo

import mu.KotlinLogging
import org.springframework.data.redis.serializer.RedisSerializer

private val log = KotlinLogging.logger {}

class KryoRedisSerializer(private val kryoHelper: KryoHelper = KryoHelper()) : RedisSerializer<Any> {

    override fun serialize(t: Any?): ByteArray? {
        if(t == null) {
            return null
        }
        return kryoHelper.writeClassAndObject(t)
    }

    override fun deserialize(bytes: ByteArray?): Any? {
        if(bytes == null || bytes.isEmpty()) {
            return null;
        }
        try {
            return kryoHelper.readClassAndObject(bytes)
        } catch(e: Exception) {
//            log.warn(e.message, e);
            return null;
        }
    }
}
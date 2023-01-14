package my.kopring.setting.scheduler.feeder.repository

import my.kopring.setting.cache.RedisValueCommander
import org.springframework.stereotype.Repository

@Repository
class OccupyRedisRepository : RedisValueCommander<Number>() {
    override fun baseKey(): String {
        return "occupy"
    }

    override fun cleanup(objKey: String?) {
    }

    private fun buildKey(prefix: String, itemKey: String): String? {
        return java.lang.String.join(":", prefix, itemKey)
    }

    fun occupy(prefix: String?, ttl: Long): Boolean {
        return setIfNotExist(prefix, System.currentTimeMillis(), ttl)
    }

    fun occupy(prefix: String?, itemKey: String?, ttl: Long): Boolean {
        return setIfNotExist(buildKey(prefix!!, itemKey!!), System.currentTimeMillis(), ttl)
    }

    fun finish(prefix: String?, ttl: Long) {
        setTTL(prefix, ttl)
    }

    fun finish(prefix: String?, itemKey: String?, ttl: Long) {
        setTTL(buildKey(prefix!!, itemKey!!), ttl)
    }
}
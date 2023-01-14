package my.kopring.setting.repository.redis

import my.kopring.setting.cache.RedisValueCommander
import org.springframework.stereotype.Repository

@Repository
class TimeStampRedisRepository : RedisValueCommander<String>() {
    override fun baseKey(): String {
        return "ts"
    }

    override fun cleanup(objKey: String?) {
    }
}
package my.kopring.setting.repository.redis

import my.kopring.setting.cache.RedisValueCommander
import org.springframework.stereotype.Repository

@Repository
class ReactHashRedisRepository : RedisValueCommander<String>(){
    override fun isShared(): Boolean {
        return true
    }

    override fun baseKey(): String {
        return "reacthash"
    }

    override fun cleanup(objKey: String?) {
    }

}
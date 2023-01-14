package my.kopring.setting.repository.redis

import my.kopring.setting.cache.RedisValueCommander
import my.kopring.setting.const.Constants
import org.springframework.stereotype.Repository

@Repository
class InvalidatedAccessTokenRepository : RedisValueCommander<Long>() {
    override fun baseKey(): String {
        return "invalidatedAccessToken"
    }

    override fun cleanup(objKey: String?) {
    }

    fun setInvalidatedAt(userId: String) {
        set(userId, System.currentTimeMillis(), Constants.EXPIRE_FOR_INVALID_TOKEN_REMOVE)
    }

    fun getInvalidatedAt(userId: String): Long? {
        return get(userId)
    }
}
package my.kopring.setting.repository.redis

import my.kopring.setting.cache.RedisValueCommander
import org.springframework.stereotype.Repository

@Repository
class RequestThrottlingRepository : RedisValueCommander<Number>() {
    override fun baseKey(): String {
        return "live:throttle"
    }

    override fun cleanup(objKey: String?) {
    }

    private fun blockKey(key: String): String{
        return "b_$key"
    }

    fun isBlocked(key: String): Boolean {
        val remainTime = get(blockKey(key))
        return remainTime != null && remainTime.toLong() > 0
    }

    fun block(key: String, duration: Long) {
        val blockKey = blockKey(key)
        set(blockKey, duration)
        setTTL(blockKey, duration)
    }

    fun append(key: String, delta: Long, checkingDuration: Long): Long? {
        val result = increment(key, delta)
        if(delta == result) { // 첫입력으로 부터 시간이 카운팅 된다.
            setTTL(key, checkingDuration)
        }
        return result
    }
}
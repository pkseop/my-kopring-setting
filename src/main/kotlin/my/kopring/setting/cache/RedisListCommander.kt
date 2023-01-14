package my.kopring.setting.cache

import my.kopring.setting.const.Constants
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

abstract class RedisListCommander<T> {
    private var PREFIX = "my"

    @Value("\${spring.profiles.active:default}-\${cache.version}")
    private lateinit var profile: String

    @Resource(name = "redisTemplate")
    private lateinit var redisTemplate: RedisTemplate<String, T>

    @Resource(name = "redisTemplate")
    private lateinit var listOp: ListOperations<String, T>

    constructor() {
        if(isShared()) {
            this.PREFIX = "shared"
        }
    }

    open fun isShared(): Boolean {
        return false
    }

    protected abstract fun baseKey(): String

    abstract fun cleanup(objKey: String?)

    protected open fun systemKey(): String? {
        return listOf(profile, PREFIX).joinToString(":")
    }

    private fun hashKey(objKey: String): String? {
        return listOf(systemKey(), baseKey(), objKey).joinToString(":")
    }

    open fun push(objKey: String?, obj: T) {
        push(objKey, obj, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun push(objKey: String?, obj: T, ttl: Long) {
        val listKey = hashKey(objKey!!)
        listOp.leftPush(listKey!!, obj)
        redisTemplate.expire(listKey!!, ttl, TimeUnit.MILLISECONDS)
    }

    open fun pop(objKey: String?): T? {
        return pop(objKey, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun pop(objKey: String?, ttl: Long): T? {
        val listKey = hashKey(objKey!!)
        val obj = listOp.rightPop(listKey!!)
        redisTemplate.expire(listKey!!, ttl, TimeUnit.MILLISECONDS)
        return obj
    }

    open fun deleteAll(objKey: String?) {
        val listKey = hashKey(objKey!!)
        redisTemplate.delete(listKey!!)
    }

    open fun setTTL(objKey: String?, duration: Long) {
        redisTemplate.expire(hashKey(objKey!!)!!, duration, TimeUnit.MILLISECONDS)
    }

    open fun getTTL(objKey: String?): Long {
        return redisTemplate.getExpire(hashKey(objKey!!)!!)
    }

}
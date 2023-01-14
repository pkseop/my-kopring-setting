package my.kopring.setting.cache

import my.kopring.setting.const.Constants
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

abstract class RedisValueCommander<T> {

    private var PREFIX = "lms"

    @Value("\${spring.profiles.active:default}-\${cache.version}")
    private lateinit var profile: String

    @Resource(name = "redisTemplate")
    private lateinit var valOp: ValueOperations<String, T>

    @Resource(name = "redisTemplate")
    private lateinit var redisTemplate: RedisTemplate<String, T>

    constructor() {
        if (isShared()) {
            PREFIX = "shared"
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

    private fun hashKey(): String {
        return listOf(systemKey(), baseKey()).joinToString(":")
    }

    private fun hashKey(objKey: String?): String {
        return listOf(systemKey(), baseKey(), objKey).joinToString(":")
    }

    open fun set(obj: T) {
        valOp.set(hashKey(), obj)
    }

    open fun set(objKey: String, obj: T) {
        set(objKey, obj, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun set(objKey: String, obj: T, ttl: Long) {
        val key = hashKey(objKey)
        valOp[key] = obj
        redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS)
    }

    open fun get(): T? {
        return valOp.get(hashKey())
    }

    open fun get(objKey: String?): T? {
        return valOp[hashKey(objKey)]
    }

    open fun getAndSet(objKey: String?, obj: T): T? {
        val key = hashKey(objKey)
        return valOp.getAndSet(key, obj)
    }

    open fun getAndSet(objKey: String?, obj: T, ttl: Long): T? {
        val key = hashKey(objKey)
        val getObj = valOp.getAndSet(key, obj)
        redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS)
        return getObj
    }

    open fun delete(objKey: String?) {
        redisTemplate.delete(hashKey(objKey))
    }

    open fun contains(objKey: String?): Boolean {
        return redisTemplate.hasKey(hashKey(objKey))
    }

    open fun setIfNotExist(objKey: String?, obj: T, ttl: Long): Boolean {
        return valOp.setIfAbsent(hashKey(objKey), obj, ttl, TimeUnit.MILLISECONDS)!!
    }

    open fun setIfNotExist(objKey: String?, obj: T): Boolean {
        return valOp.setIfAbsent(hashKey(objKey), obj)!!
    }

    open fun setTTL(objKey: String?, duration: Long) {
        redisTemplate.expire(hashKey(objKey), duration, TimeUnit.MILLISECONDS)
    }

    open fun getTTL(objKey: String?): Long {
        return redisTemplate.getExpire(hashKey(objKey))
    }

    open fun increment(hashKey: String?): Long? {
        val key = hashKey(hashKey)
        return valOp.increment(key)!!
    }

    open fun increment(hashKey: String?, delta: Long): Long? {
        val key = hashKey(hashKey)
        return valOp.increment(key, delta)!!
    }
}
package my.kopring.setting.cache

import com.fasterxml.jackson.databind.ObjectMapper
import my.kopring.setting.const.Constants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.HashOperations
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

abstract class RedisHashCommander<T> {
    private var PREFIX = "my"

    @Value("\${spring.profiles.active:default}-\${cache.version}")
    private lateinit var profile: String

    @Resource(name = "redisTemplate")
    private lateinit var hashOp: HashOperations<String?, String?, T>

    @Resource(name = "redisTemplate")
    private lateinit var redisTemplate: RedisTemplate<String?, T>

    @Autowired
    private lateinit var mapper: ObjectMapper

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

    protected open fun systemKey(): String {
        return listOf<String>(profile, PREFIX).joinToString(":")
    }

    protected open fun buildKey(hashKey: String?): String? {
        return if (hashKey == null) {
            listOf(systemKey(), baseKey()).joinToString(":")
        } else {
            listOf(systemKey(), baseKey(), hashKey).joinToString(":")
        }
    }

    open fun set(objKey: String, obj: T) {
        set(null, objKey, obj)
    }

    open fun set(hashKey: String?, objKey: String, obj: T) {
        set(hashKey, objKey, obj, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun set(hashKey: String?, objKey: String, obj: T, ttl: Long) {
        val key = buildKey(hashKey)
        hashOp.put(key!!, objKey!!, obj)
        redisTemplate.expire(key, ttl, TimeUnit.MILLISECONDS)
    }

    open fun increment(hashKey: String?, objKey: String, delta: Long): Long? {
        return increment(hashKey, objKey, delta, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun increment(hashKey: String?, objKey: String, delta: Long, ttl: Long): Long? {
        val key = buildKey(hashKey)
        val result = hashOp.increment(key!!, objKey, delta)
        redisTemplate.expire(key!!, ttl, TimeUnit.MILLISECONDS)
        return result
    }

    open fun setIfNotExist(objKey: String, obj: T): Boolean {
        return setIfNotExist(null, objKey, obj)
    }

    open fun setIfNotExist(hashKey: String?, objKey: String, obj: T): Boolean {
        return setIfNotExist(hashKey, objKey, obj, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun setIfNotExist(hashKey: String?, objKey: String, obj: T, ttl: Long): Boolean {
        val key = buildKey(hashKey)
        val result = hashOp.putIfAbsent(key!!, objKey!!, obj)
        redisTemplate.expire(key!!, ttl, TimeUnit.MILLISECONDS)
        return result
    }

    open fun exist(hashKey: String?, objKey: String): Boolean {
        return hashOp.hasKey(buildKey(hashKey)!!, objKey)
    }

    open fun exist(objKey: String): Boolean {
        return exist(null, objKey)
    }

    open fun get(hashKey: String?, objKey: String): T? {
        return hashOp[buildKey(hashKey)!!, objKey]
    }

    open fun get(objKey: String): T? {
        return get(null, objKey)
    }

    open fun keys(): Set<String?>? {
        return hashOp.keys(buildKey(null)!!)
    }

    open fun keys(hashKey: String?): Set<String?>? {
        return hashOp.keys(buildKey(hashKey)!!)
    }

    open fun values(hashKey: String?): List<T>? {
        return hashOp.values(buildKey(hashKey)!!)
    }

    open fun values(): List<T>? {
        return values(null)
    }

    open fun delete(hashKey: String?, objKey: String?): Boolean {
        return hashOp.delete(buildKey(hashKey)!!, objKey) != null
    }

    open fun delete(objKey: String): Boolean {
        return delete(null, objKey)
    }

    open fun contains(hashKey: String?, objKey: String): Boolean {
        return hashOp.hasKey(buildKey(hashKey)!!, objKey)
    }

    open fun contains(objKey: String): Boolean {
        return contains(null, objKey)
    }

    open fun deleteAll(hashKey: String?) {
        redisTemplate.delete(buildKey(hashKey)!!)
    }

    open fun deleteAll() {
        deleteAll(null)
    }

    open fun toMap(): Map<String?, T>? {
        return toMap(null)
    }

    open fun toMap(hashKey: String?): Map<String?, T>? {
        return hashOp.entries(buildKey(hashKey)!!)
    }

    open fun setTTL(objKey: String?, duration: Long) {
        redisTemplate.expire(buildKey(objKey)!!, duration, TimeUnit.MILLISECONDS)
    }

    open fun getTTL(objKey: String?): Long {
        return redisTemplate.getExpire(buildKey(objKey)!!)
    }
}
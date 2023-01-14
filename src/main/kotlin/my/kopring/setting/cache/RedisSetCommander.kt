package my.kopring.setting.cache

import my.kopring.setting.const.Constants
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.SetOperations
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

abstract class RedisSetCommander<T> {
    private var PREFIX = "lms"

    @Value("\${spring.profiles.active:default}-\${cache.version}")
    private lateinit var profile: String

    @Resource(name = "redisTemplate")
    private lateinit var setOp: SetOperations<String, T>

    @Resource(name = "redisTemplate")
    private lateinit var redisTemplate: RedisTemplate<String, T>

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

    private fun buildKey(setsKey: String?): String? {
        return if (setsKey == null) {
            listOf(profile, PREFIX, baseKey()).joinToString(":")
        } else {
            listOf(profile, PREFIX, baseKey(), setsKey).joinToString(":")
        }
    }

    open fun add(setsKey: String?, objKey: T): Boolean {
        return add(setsKey, objKey, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun add(setsKey: String?, objKey: T, ttl: Long): Boolean {
        val key = buildKey(setsKey)
        val affectedRows = setOp.add(key!!, objKey)
        redisTemplate.expire(key!!, ttl, TimeUnit.MILLISECONDS)
        return affectedRows != null && affectedRows > 0
    }

    open fun add(objKey: T): Boolean {
        return add(null, objKey)
    }

    open fun delete(objKey: T): Boolean {
        return delete(null, objKey)
    }

    open fun delete(setsKey: String?, objKey: T): Boolean {
        val affectedRows = setOp.remove(buildKey(setsKey)!!, objKey)
        return affectedRows != null && affectedRows > 0
    }

    open fun deleteAll(setsKey: String?) {
        redisTemplate.delete(buildKey(setsKey)!!)
    }

    open fun deleteAll() {
        deleteAll(null)
    }

    open fun pop(setsKey: String?): T? {
        return setOp.pop(buildKey(setsKey)!!)
    }

    open fun pop(): T? {
        return pop(null)
    }

    open fun size(setsKey: String?): Long {
        val size = setOp.size(buildKey(setsKey)!!)
        return size ?: 0
    }

    open fun size(): Long {
        return size(null)
    }

    open fun isMember(setsKey: String?, objKey: T): Boolean {
        val has: Boolean? = setOp.isMember(buildKey(setsKey)!!, objKey as Any)
        return has ?: false
    }

    open fun getAll(setsKey: String?): List<T> {
        val members = setOp.members(buildKey(setsKey)!!)
        return if (members == null) emptyList() else members.toList()
    }

    open fun setTTL(objKey: String?, duration: Long) {
        redisTemplate.expire(buildKey(objKey)!!, duration, TimeUnit.MILLISECONDS)
    }

    open fun getTTL(objKey: String?): Long {
        return redisTemplate.getExpire(buildKey(objKey)!!)
    }

    open fun startTrasaction() {
        redisTemplate.multi()
    }

    open fun commitTransaction() {
        redisTemplate.exec()
    }

    open fun cancelTransaction() {
        redisTemplate.discard()
    }

    open fun keys(pattern: String?): Set<String?>? {
        val key = buildKey(pattern)
        return redisTemplate.keys(key!!)
    }

}
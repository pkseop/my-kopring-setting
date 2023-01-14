package my.kopring.setting.cache

import biz.gripcloud.lms.base.cache.Scoreable
import com.google.common.collect.Lists
import my.kopring.setting.const.Constants
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import java.util.concurrent.TimeUnit
import javax.annotation.Resource

abstract class RedisZSetCommander<T> {

    private var PREFIX = "lms"

    @Value("\${spring.profiles.active:default}-\${cache.version}")
    private lateinit var profile: String

    @Resource(name = "redisTemplate")
    private lateinit var setOp: ZSetOperations<String, T>

    @Resource(name = "redisTemplate")
    private lateinit var redisTemplate: RedisTemplate<String, T>

    val EMPTY: List<T> = emptyList()

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

    open fun add(objKey: T) {
        add(null, objKey)
    }

    open fun add(setsKey: String?, objKey: T) {
        add(setsKey, objKey, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun add(setsKey: String?, objKey: T, ttl: Long) {
        val key = buildKey(setsKey)
        if (objKey is Scoreable) {
            val scorableObj: Scoreable = objKey as Scoreable
            setOp.add(key!!, objKey, scorableObj.score().toDouble())
        } else {
            setOp.add(key!!, objKey, 0.0)
        }
        redisTemplate.expire(key!!, ttl, TimeUnit.MILLISECONDS)
    }

    open fun addWithScore(obj: T, score: Long) {
        val key = buildKey(null)
        setOp.add(key!!, obj, score.toDouble())
    }

    open fun addWithScore(obj: T, score: Long, ttl: Long) {
        val key = buildKey(null)
        setOp.add(key!!, obj, score.toDouble())
        redisTemplate.expire(key!!, ttl, TimeUnit.MILLISECONDS)
    }

    open fun addWithScore(setsKey: String?, obj: T, score: Long, ttl: Long) {
        val key = buildKey(setsKey)
        setOp.add(key!!, obj, score.toDouble())
        redisTemplate.expire(key!!, ttl, TimeUnit.MILLISECONDS)
    }

    open fun delete(objKey: T) {
        delete(null, objKey)
    }

    open fun delete(setsKey: String?, objKey: T) {
        setOp.remove(buildKey(setsKey)!!, objKey)
    }

    open fun deleteAll(setsKey: String?) {
        redisTemplate.delete(buildKey(setsKey)!!)
    }

    open fun deleteAll() {
        deleteAll(null)
    }

    open fun pop(setsKey: String?): T? {
        return pop(setsKey, Constants.EXPIRE_FOR_REMOVE)
    }

    open fun pop(setsKey: String?, ttl: Long): T? {
        val key = buildKey(setsKey)
        val size = setOp.size(key!!)
        if (size == null || size == 0L) {
            return null
        }
        val elements = setOp.range(key!!, size - 1, size)
        if (elements == null || elements.size == 0) {
            return null
        }
        val obj = Lists.newArrayList(elements)[0]
        setOp.removeRange(key!!, size - 1, size)
        redisTemplate.expire(key!!, ttl, TimeUnit.MILLISECONDS)
        return obj
    }

    open fun rank(setsKey: String?, obj: T): Long {
        val rank = setOp.rank(buildKey(setsKey)!!, obj as Any)
        return rank ?: 0
    }

    open fun popHigh(): T? {
        return pop(null)
    }

    open fun size(setsKey: String?): Long {
        val size = setOp.size(buildKey(setsKey)!!)
        return size ?: 0
    }

    open fun size(): Long {
        return size(null)
    }

    open fun getAll(setsKey: String?): List<T>? {
        val key = buildKey(setsKey)
        val size = setOp.size(key!!) ?: return EMPTY
        val elements = setOp.range(key!!, size - 1, size)
        return if (elements == null || elements.size == 0) {
            EMPTY
        } else elements.toList()
    }

    open fun getListByScoreRange(setsKey: String?, startScore: Long, endScore: Long): List<T> {
        val key = buildKey(setsKey)
        val size = setOp.size(key!!) ?: return EMPTY
        val elements = setOp.rangeByScore(key!!, startScore.toDouble(), endScore.toDouble())
        return if (elements == null || elements.size == 0) {
            EMPTY
        } else elements.toList()
    }

    open fun deleteByScoreRange(setsKey: String?, startScore: Long, endScore: Long): Int {
        val key = buildKey(setsKey)
        val size = setOp.size(key!!) ?: return 0
        val elements = setOp.rangeByScore(key!!, startScore.toDouble(), endScore.toDouble())
        if (elements == null || elements.size == 0) {
            return 0
        }
        for (objKey in elements) {
            setOp.remove(key!!, objKey)
        }
        return elements.size
    }

    open fun setTTL(objKey: String?, duration: Long) {
        redisTemplate.expire(buildKey(objKey)!!, duration, TimeUnit.MILLISECONDS)
    }

    open fun getTTL(objKey: String?): Long {
        return redisTemplate.getExpire(buildKey(objKey)!!)
    }
}
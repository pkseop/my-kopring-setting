package my.kopring.setting.utils

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import mu.KotlinLogging
import my.kopring.setting.component.ActiveProfile
import my.kopring.setting.repository.redis.ReactHashRedisRepository
import my.kopring.setting.repository.redis.TimeStampRedisRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import javax.annotation.PostConstruct

private val log = KotlinLogging.logger {}

@Component("tUtil")
class ThymeleafUtils(
    @Value("\${domain.resource}")
    val resourceDomain: String,
    @Value("\${domain.static}")
    val staticDomain: String,

    private val activeProfile: ActiveProfile
) {
    var localTS: String? = null
    var tsUpdateTime = System.currentTimeMillis()

    var useMinify = false
    var useTs = false
    var isReal = false

    @Autowired
    private lateinit var timeStampRedisRepository: TimeStampRedisRepository
    @Autowired
    private lateinit var reactHashRedisRepository: ReactHashRedisRepository

    private lateinit var localHashMap: LoadingCache<String, String?>

    @PostConstruct
    fun postMethod() {
        if (activeProfile.isProd()) {
            useMinify = true
            useTs = true
        } else if (activeProfile.isDev()) {
            useMinify = true
            useTs = true
        } else {
            useMinify = false
            useTs = false
        }

        useTs = true
        localTS = (System.currentTimeMillis() / 1000).toString() // 마지막 부트업하는 서버가 TS값을 설정한다.

        timeStampRedisRepository.set(localTS!!)

        localHashMap = CacheBuilder.newBuilder() //
            .maximumSize(100) //
            .expireAfterWrite(10, TimeUnit.SECONDS) //
            .build(object : CacheLoader<String, String?>() {
                @Throws(Exception::class)
                override fun load(key: String): String {
                    return reactHashRedisRepository.get(key)!!
                }
            })
    }

    // 10초간 로컬 캐시 불필요하게 redis를 호출 하는것도 불필요하다.
    fun getTs(): String? {
        val current = System.currentTimeMillis()
        if (tsUpdateTime + 10000 > current) {
            return localTS
        }
        tsUpdateTime = current
        localTS = timeStampRedisRepository.get()
        return localTS
    }

    fun css(path: String): String {
        // 우선 real인지만 판단해서 minify를 사용하지 않도록 한다.
        // FIXME 위의 조건대로 조합한다.
        val b: StringBuilder = StringBuilder(resourceDomain).append(path)
        if (useMinify) {
            b.append(".min.css")
        } else {
            b.append(".css")
        }
        if (useTs) {
            b.append("?ts=").append(getTs())
        }

        return b.toString()
    }

    fun scss(path: String): String {
        val b = StringBuilder(resourceDomain)
        if (useMinify) {
            b.append("/css").append(path).append(".min.css")
        } else {
            b.append("/scss/build").append(path).append(".css")
        }
        if (useTs) {
            b.append("?ts=").append(getTs())
        }
        return b.toString()
    }

    fun js(path: String): String {
        val b = StringBuilder(resourceDomain).append(path)
        if (useMinify) {
            b.append(".min.js")
        } else {
            b.append(".js")
        }
        if (useTs) {
            b.append("?ts=").append(getTs())
        }
        return b.toString()
    }

    fun hashCss(target: String, path: String): String? {
        try {
            val hash = localHashMap[target]

            val b = StringBuilder()
//            if (activeProfile.isLocal()) {
//                b.append(resourceDomain).append("/css/mini/")
//            } else {
                b.append(staticDomain).append("/resource/reacthash/").append(target).append("/").append(hash).append("/")
//            }

            b.append(path).append(".").append(hash).append(".css")
            return b.toString()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return path
    }

    fun hashJs(target: String, path: String): String {
        try {
            val hash = localHashMap[target]
            val b = StringBuilder()
//            if (activeProfile.isLocal()) {
//                b.append(resourceDomain).append("/js/external/mini/")
//            } else {
                b.append(staticDomain).append("/resource/reacthash/").append(target).append("/").append(hash).append("/")
//            }
            b.append(path).append(".").append(hash).append(".js")
            return b.toString()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return path
    }

    fun hash(target: String?, path: String?): String? {
        try {
            val b = StringBuilder(resourceDomain).append(path).append(".").append(localHashMap[target!!]).append(".js")
            return b.toString()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return path
    }

    fun plain(path: String?, useTs: Boolean): String? {
        val b = StringBuilder(resourceDomain).append(path)
        if (this.useTs && useTs) {
            b.append("?ts=").append(getTs())
        }
        return b.toString()
    }

    fun plain(path: String?): String? {
        return plain(path, false)
    }

    fun newline(text: String?): String? {
        return if (!text.isNullOrBlank()) {
            text.replace("\n", "<br/>")
        } else null
    }

    fun isEmpty(text: String?): Boolean {
        return text == null || text == ""
    }


}